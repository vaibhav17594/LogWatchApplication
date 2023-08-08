package com.logwatcher.logwatcherapp.util;

import com.logwatcher.logwatcherapp.entities.event.Event;
import com.logwatcher.logwatcherapp.entities.event.LogEvent;
import com.logwatcher.logwatcherapp.service.LogProcessorService;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogWatcher {

    private Queue<Event> tailLogs;
    private LogProcessorService logProcessorService;
    private String logfile;
    private File file;
    private Path parentDirectory;
    private RandomAccessFile randomAccessFile;
    private WatchService watcher;
    private Long filePosition;
    private AtomicBoolean isActive;

    public LogWatcher(LogProcessorService logProcessorService, String logfile) throws IOException {
        this.tailLogs = new LinkedList<>();
        this.logProcessorService = logProcessorService;
        this.logfile = logfile;
        this.file = new File(this.logfile);
        this.randomAccessFile = new RandomAccessFile(this.file, "r");
        this.watcher = FileSystems.getDefault().newWatchService();
        this.parentDirectory = Paths.get(this.file.getParent());
        this.parentDirectory.register(this.watcher, StandardWatchEventKinds.ENTRY_MODIFY);
        this.filePosition = 0L;
        this.isActive = new AtomicBoolean();
        CompletableFuture.runAsync(this::start);
    }

    private void start() {
        try {
            this.isActive.set(true);
            this.readFileContent(this.parentDirectory.resolve(this.file.getName()));
            while (true) {
                if (!isActive.get()) {
                    break;
                }
                WatchKey watchKey = watcher.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    Path eventPath = (Path) event.context();
                    if (eventPath.toString().equals(this.file.getName())) {
                        readFileContent(this.parentDirectory.resolve(eventPath));
                    }
                }
                watchKey.reset();
            }
        } catch (Exception exception) {
            //
        }
    }

    private void readFileContent(Path filePath) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "r");
        try {
            randomAccessFile.seek(this.filePosition);
            String logline;
            while((logline = randomAccessFile.readLine()) != null) {
                LogEvent logEvent = buildEvent(filePath, logline);
                appendEvent(logEvent);
                this.logProcessorService.processLog(logEvent);
            }
            this.filePosition = randomAccessFile.getFilePointer();
        } catch (Exception exception) {
            //
        }
    }

    private LogEvent buildEvent(Path filePath, String logline) {
        return LogEvent.builder()
                .content(logline)
                .logfile(filePath.toString())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    private void appendEvent(LogEvent logEvent) {
        this.tailLogs.add(logEvent);
    }

    public void stop() {
        this.isActive.set(false);
        this.tailLogs.clear();
    }

    public Set<Event> getAllLogEvents() {
        return new TreeSet<>(this.tailLogs);
    }
}
