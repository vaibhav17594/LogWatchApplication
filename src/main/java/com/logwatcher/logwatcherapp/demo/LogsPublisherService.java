package com.logwatcher.logwatcherapp.demo;

import com.logwatcher.logwatcherapp.repository.LogfilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LogsPublisherService {

    private List<String> filesInRepository;
    private LogfilesRepository logfilesRepository;
    private ExecutorService executorService;

    @Autowired
    public LogsPublisherService(@Value("#{'${log.files}'.split(',')}")  List<String> filesInRepository, LogfilesRepository logfilesRepository) {
        this.filesInRepository = filesInRepository;
        this.logfilesRepository = logfilesRepository;
        this.executorService = Executors.newWorkStealingPool();
        this.filesInRepository.forEach(file -> {
            this.logfilesRepository.addFile(file, new Object());
            this.executorService.submit(() -> this.start(file));
        });
    }

    private void start(String fileToProduceLogsTo) {

        try {
            int counter = 0;
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (Exception exception) {
                    //
                }
                StringBuilder content = new StringBuilder();
                content.append("Message for app: " + fileToProduceLogsTo.split("resources/")[1] + " - " + counter++);
                Files.write(Paths.get(fileToProduceLogsTo), content.toString().getBytes(), StandardOpenOption.APPEND);
                Files.write(Paths.get(fileToProduceLogsTo), System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
