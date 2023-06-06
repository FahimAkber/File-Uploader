package com.example.fileuploader.controller;

import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.JobRequest;
import com.example.fileuploader.model.SchedulerRequest;
import com.example.fileuploader.model.ServerInfo;
import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.service.QuartzJobInfoService;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.quartzscheduler.QuartzSchedulerService;
import com.example.fileuploader.service.ServerService;
import com.example.fileuploader.transferfile.FileTransferService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.tomcat.jni.Local;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/file-transfer")
public class FileTransferController {

    private final QuartzJobInfoService quartzJobInfoService;
    private final QuartzSchedulerService quartzSchedulerService;
    private final ServerService serverService;

    public FileTransferController(QuartzJobInfoService quartzJobInfoService, QuartzSchedulerService quartzSchedulerService, ServerService serverService) {
        this.quartzJobInfoService = quartzJobInfoService;
        this.quartzSchedulerService = quartzSchedulerService;
        this.serverService = serverService;
    }

    @PostMapping("/schedule-file")
    public ResponseEntity<Object> schedulingFile(@RequestBody SchedulerRequest schedulerRequest) {
        JobDetail jobDetail = quartzSchedulerService.getJobByKey(new JobKey(schedulerRequest.getJobKey()));
        quartzSchedulerService.saveTrigger(jobDetail, schedulerRequest.getTotalInterval(), schedulerRequest.getFrequency(), schedulerRequest.getStartAt());

        return ResponseEntity.ok("Successfully schedule the job");
    }

    @PostMapping(value = "/save-job-info")
    public ResponseEntity<Object> saveJobInfo(@RequestBody JobInfo jobInfo) {
        return ResponseEntity.ok(quartzJobInfoService.saveQuartzJob(jobInfo));
    }

    @GetMapping(value = "/get-job-info")
    public ResponseEntity<Object> getJobInfo(@RequestParam("page") Integer pageNo, @RequestParam("size") Integer pageSize) {
        return ResponseEntity.ok(quartzJobInfoService.getQuartzJobInfos(pageNo, pageSize));
    }

    @DeleteMapping("/job-info/delete/{jobKey}")
    public ResponseEntity<Object> deleteJobInfo(@PathVariable("jobKey") String jobKey){
        return ResponseEntity.ok(quartzJobInfoService.deleteJobInfo(jobKey));
    }

    @GetMapping("get-job/{jobKey}")
    public ResponseEntity<Object> getJob(@PathVariable String jobKey) {
        try {
            JobKey key = new JobKey(jobKey);
            JobDetail jobByKey = quartzSchedulerService.getJobByKey(key);

            return ResponseEntity.ok(jobByKey);
        } catch (FileUploaderException exception) {
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @GetMapping("cancel-running-job/{jobKey}")
    public ResponseEntity<Object> cancelTrigger(@PathVariable String jobKey) {
        try {
            JobKey key = new JobKey(jobKey);
            quartzSchedulerService.cancelTrigger(key);

            return ResponseEntity.ok("Cancel running jobs by key: " + key.getName());
        } catch (FileUploaderException exception) {
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @GetMapping("resume-running-job/{jobKey}")
    public ResponseEntity<Object> resumeJob(@PathVariable String jobKey) {
        try {
            JobKey key = new JobKey(jobKey);
            quartzSchedulerService.resumeJob(key);

            return ResponseEntity.ok("Resume jobs by key: " + key.getName());
        } catch (FileUploaderException exception) {
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @GetMapping("pause-job/{jobKey}")
    public ResponseEntity<Object> pauseJob(@PathVariable String jobKey) {
        try {
            JobKey key = new JobKey(jobKey);
            quartzSchedulerService.pauseJob(key);

            return ResponseEntity.ok("Pause job by key: " + key.getName());
        } catch (FileUploaderException exception) {
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @PostMapping("update-job")
    public ResponseEntity<Object> updateJob(@RequestBody JobRequest jobRequest) {
        try {
            quartzSchedulerService.updateJob(jobRequest);
            return ResponseEntity.ok("Job updated successfully.");
        } catch (FileUploaderException exception) {
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @PostMapping("schedule-private-job")
    public ResponseEntity<Object> schedulingPrivateJob(@RequestBody SchedulerRequest schedulerRequest) {
        int totalInterval = schedulerRequest.getTotalInterval();
        int frequency = schedulerRequest.getFrequency();
        Date startAt = schedulerRequest.getStartAt();
        String jobType = schedulerRequest.getJobType();

        quartzSchedulerService.savePrivateJob(jobType, totalInterval, frequency, startAt);

        return ResponseEntity.ok("Successfully schedule the job");
    }

    @GetMapping("folder_count/{id}")
    public void getFolderCount(@PathVariable("id") Long id) throws Exception {
        ChannelSftp channelSftp = null;
        Server sourceServer = serverService.findById(id);

        Session session = null;
        JSch jSch = new JSch();
        try {
            if (sourceServer.getSecureFileName() != null && !sourceServer.getSecureFileName().trim().isEmpty()) {
                jSch.addIdentity(sourceServer.getSecureFileName());
            } else {
                session.setPassword(sourceServer.getPassword());
            }
            session = jSch.getSession(sourceServer.getUser(), sourceServer.getHost(), sourceServer.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("compression.s2c", "zlib,none");
            session.setConfig("compression.c2s", "zlib,none");
            session.setConfig("rcvbuf", "1048576"); // 1 MB
            session.setConfig("sndbuf", "1048576");
            session.setConfig("sftp.max_packet", "131072"); // Set maximum packet size to 131072 bytes (128 KB)

            session.connect();
            LocalDateTime startTime = LocalDateTime.now();


            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.cd("/main_folder/");
            Vector<ChannelSftp.LsEntry> ls = channelSftp.ls("/files/*");
            LoggerFactory.getLogger("Test Log").info("Iteration of folders start at: {}", startTime);
            for (ChannelSftp.LsEntry entry : ls){
                LocalDateTime itemStart = LocalDateTime.now();
                channelSftp.cd("/main_folder/" + entry.getFilename() + "/");
                channelSftp.mkdir("child_4");
                LocalDateTime itemEnd = LocalDateTime.now();
                Duration timeDuration = Duration.between(itemStart, itemEnd);
                LoggerFactory.getLogger("Item Log").info("Item visit history. start at: {}, end at: {}, total time: {}", itemStart, itemEnd, timeDuration.getSeconds());
            }
            LocalDateTime endTime = LocalDateTime.now();
            Duration duration = Duration.between(startTime, endTime);
            LoggerFactory.getLogger("Test Log").info("Iteration of folders start at: {}, end at: {}, total time: {}", startTime, endTime, duration.getSeconds());
        } catch (Exception e) {
            LoggerFactory.getLogger("Error Log").info(e.getMessage() + " at "+ LocalDateTime.now());
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            LoggerFactory.getLogger("Final Log").info("Process completed at "+ LocalDateTime.now());
            channelSftp.disconnect();
            session.disconnect();
        }

    }
}

