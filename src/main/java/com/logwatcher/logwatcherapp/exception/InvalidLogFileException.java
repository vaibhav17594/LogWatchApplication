package com.logwatcher.logwatcherapp.exception;

public class InvalidLogFileException extends Exception {

    public InvalidLogFileException(String message) {
        super(message);
    }

    public InvalidLogFileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
