package com.example.fileuploader.configuration;

import com.jcraft.jsch.SftpProgressMonitor;

public class FIleProgressMonitor implements SftpProgressMonitor {
    private long totalBytes;
    private long transferredBytes;
    private double progress;
    @Override
    public void init(int i, String s, String s1, long l) {
        totalBytes = l;
    }

    @Override
    public boolean count(long l) {
        transferredBytes += l;
        progress = (double) transferredBytes/totalBytes;
        System.out.printf("Transferred: %d/%d bytes (%.2f%%)\n", transferredBytes, totalBytes, progress * 100);

        return true;
    }

    @Override
    public void end() {
        totalBytes = 0;
        transferredBytes = 0;
        progress = 0;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public long getTransferredBytes() {
        return transferredBytes;
    }

    public double getProgress() {
        return progress;
    }
}
