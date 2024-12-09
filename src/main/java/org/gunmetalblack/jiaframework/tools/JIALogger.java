package org.gunmetalblack.jiaframework.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/*
    Code inspired from https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html
    https://stackoverflow.com/questions/5950557/good-examples-using-java-util-logging
    https://www.loggly.com/ultimate-guide/java-logging-basics/
* */
public class JIALogger {
    private static final String LOG_FILE = "JIA.log";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum LogLevel {
        INFO,
        WARN,
        ERROR
    }

    /**
     * Logs a message with varying levels of severity
     * @param level
     * @param message
     */
    public static void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);
        System.out.println(logMessage);
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(logMessage + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Failed to write to log file zamn!: " + e.getMessage());
        }
    }


    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    public static void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }
}
