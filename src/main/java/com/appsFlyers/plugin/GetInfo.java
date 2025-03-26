package com.appsFlyers.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GetInfo {
    public static List<String> getConnectedDevices(String adbPath) throws IOException {
        List<String> devices = new ArrayList<>();
        ProcessBuilder builder = new ProcessBuilder(adbPath, "devices");
        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        devices.add(parts[0]);
                    }
                }
            }
        }

        return devices;
    }

    public static String getDeviceName(String adbPath, String deviceID) throws IOException {
        // אם מדובר באמולטור (ID מתחיל ב־"emulator-"):
        if (deviceID.startsWith("emulator-")) {
            // הפקודה adb -s emulator-XXXX emu avd name
            ProcessBuilder builder = new ProcessBuilder(adbPath, "-s", deviceID, "emu", "avd", "name");
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String avdName = reader.readLine(); // שם ה־AVD (שורה ראשונה)
                if (avdName != null && !avdName.trim().isEmpty()) {
                    return "Emulator: " + avdName.trim();
                } else {
                    return deviceID; // fallback
                }
            }
        } else {
            // כנראה מכשיר פיזי: ניקח model + manufacturer
            String manufacturer = getProp(adbPath, deviceID, "ro.product.manufacturer");
            String model = getProp(adbPath, deviceID, "ro.product.model");
            if (manufacturer == null) manufacturer = "";
            if (model == null) model = "";
            String fullName = (manufacturer + " " + model).trim();
            return fullName.isEmpty() ? deviceID : fullName;
        }
    }

    // פונקציה פנימית שמריצה פקודת getprop
    private static String getProp(String adbPath, String deviceID, String propName) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(adbPath, "-s", deviceID, "shell", "getprop", propName);
        Process process = builder.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            return (line != null) ? line.trim() : null;
        }
    }


    public static String getAdbPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return System.getProperty("user.home") + "\\AppData\\Local\\Android\\Sdk\\platform-tools\\adb.exe";
        } else if (osName.contains("mac")) {
            return "/Users/" + System.getProperty("user.name") + "/Library/Android/sdk/platform-tools/adb";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return System.getProperty("user.home") + "/Android/Sdk/platform-tools/adb";
        } else {
            throw new RuntimeException("Unsupported OS");
        }
    }
}
