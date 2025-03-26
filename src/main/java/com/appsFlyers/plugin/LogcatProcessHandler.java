package com.appsFlyers.plugin;

import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

public class LogcatProcessHandler {
    private static final Logger logger = Logger.getInstance(LogcatProcessHandler.class);
    private static String selectedDeviceId = null;
    private static OSProcessHandler currentProcessHandler = null;
    // Map to store raw logcat entries by timestamp
    private static final Map<String, String> rawLogcatEntries = new HashMap<>();

    public static void setSelectedDeviceId(String deviceId) {
        selectedDeviceId = deviceId;
    }

    public static void startLogcat() {
        try {
            String adbPath = GetInfo.getAdbPath();
            List<String> devices = GetInfo.getConnectedDevices(adbPath);
            if (devices.isEmpty()) {
                throw new RuntimeException("No ADB devices found");
            }

            SwingUtilities.invokeLater(() -> {
                startLoggingForDevice(selectedDeviceId, adbPath);
                // Automatically show all logs after device selection
                showLogs.filterLogs(null);
            });
        } catch (Exception e) {
            logger.error("Error starting adb logcat", e);
        }
    }

    private static void processObject(String type, String text, String date) {
        String finalLog = LogUtils.extractMessageFromJson(type, text);

        if (finalLog != null) {
            if (finalLog.contains("No deep link")) {
                SwingUtilities.invokeLater(() -> {
                    String errorLog = date + " / " + type + ":\n" + finalLog + " ERROR!";
                    showLogs.showUpdateLogs(errorLog, type, text);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    String logEntry = date + " / " + type + ":\n" + finalLog;
                    showLogs.showUpdateLogs(logEntry, type, text);
                });
            }
        }
    }
}
