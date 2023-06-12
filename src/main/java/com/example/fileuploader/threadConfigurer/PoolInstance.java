package com.example.fileuploader.threadConfigurer;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class PoolInstance {
    private ExecutorService executorService;
    private List<FileThread> tasks;

    public PoolInstance(ExecutorService executorService){
        this.executorService = executorService;
    }
    public PoolInstance(ExecutorService executorService, List<FileThread> tasks) {
        this.executorService = executorService;
        this.tasks = tasks;
    }

    public void setTasks(List<FileThread> tasks){
        this.tasks = tasks;
    }

    public List<FileThread> getTasks(){
        return this.tasks;
    }
    public void implementSingleInstance() {
        for (FileThread task : getTasks()) {
            executorService.execute(task);
        }
    }
}
