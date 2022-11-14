package com.example.fileuploader.configuration;

import com.example.fileuploader.exceptions.FileUploaderException;
import org.springframework.http.HttpStatus;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Partition<T> extends AbstractList<List<T>> {
    private final List<T> files;
    private final int chunkSize;

    private Partition(List<T> files, int chunkSize){
        this.files = files;
        this.chunkSize = chunkSize;
    }

    public static <T> Partition<T> getPartitionInstance(List<T> files, int chunkSize){
        return new Partition<>(files, chunkSize);
    }

    @Override
    public List<T> get(int i) {
        int start = i * chunkSize;
        int end = Math.min(start + chunkSize, files.size());

        if (start > end) {
            throw new FileUploaderException("Index " + i + " is out of the list range <0," + (size() - 1) + ">", HttpStatus.NO_CONTENT);
        }

        return new ArrayList<>(files.subList(start, end));
    }

    @Override
    public int size() {
        return (int) Math.ceil((double) files.size() / (double) chunkSize);
    }
}
