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

    private static void startLoggingForDevice(String deviceId, String adbPath) {
        try {
            // סגירת התהליך הקודם אם קיים ופעיל
            if (currentProcessHandler != null && !currentProcessHandler.isProcessTerminated()) {
                currentProcessHandler.destroyProcess();
            }

            ProcessBuilder builder = new ProcessBuilder(adbPath, "-s", deviceId, "logcat");
            Process process = builder.start();
            OSProcessHandler processHandler = getOsProcessHandler(process);
            currentProcessHandler = processHandler;
            processHandler.startNotify();

        } catch (Exception e) {
            logger.error("Error starting logcat for device: " + deviceId, e);
        }
    }

    private static @NotNull OSProcessHandler getOsProcessHandler(Process process) {
        OSProcessHandler processHandler = new OSProcessHandler(process, "adb logcat", StandardCharsets.UTF_8);

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                String text = event.getText();

                if (text.length() <= 18) return;

                String date = text.substring(0, 18);

                // Store the raw logcat entry for later lookup
                rawLogcatEntries.put(date, text);
                if (text.contains("CONVERSION-")) {
                    processUID("CONVERSION", text, date);
                } else if (text.contains("LAUNCH-")) {
                    processUID("LAUNCH", text, date);
                } else if (text.contains("INAPP-")) {
                    processObject("EVENT", text, date);
                } else if (text.contains("deepLink") || text.contains("No deep link")) {
                    processObject("DEEPLINK", text, date);
                }
            }
        });

        return processHandler;
    }

    private static void processUID(String type, String text, String date) {
        String formattedLog = LogUtils.extractMessageFromJson(type, text);

        if (text.contains("result:")) {
            int resIndex = text.indexOf("result");
            String shortLog = date + " / " + type + ": " + text.substring(resIndex);
            if(text.contains("FAILURE")){
                String errorShortLog = date + " / " + type + ": " + text.substring(resIndex)+ " ERROR!";
                SwingUtilities.invokeLater(() ->
                        showLogs.showUpdateLogs(errorShortLog,type+ ": result", text)
                );
                return;
            }
            SwingUtilities.invokeLater(() ->
                    showLogs.showUpdateLogs(shortLog,type + ": result", text)
            );
        } else if (formattedLog != null) {
            String shortLog = date + " / " + type + " " + formattedLog;
            SwingUtilities.invokeLater(() ->
                    showLogs.showUpdateLogs(shortLog, type + " " + formattedLog, text)
            );
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
