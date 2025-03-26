package com.appsFlyers.plugin.UI;

public class LogEntry {
    private final String shortLog;
    private final String fullLog;

    public LogEntry(String shortLog, String fullLog) {
        this.shortLog = shortLog;
        this.fullLog = fullLog;
    }

    public String getShortLog() {
        return shortLog;
    }

    public String getFullLog() {
        return fullLog;
    }
}
