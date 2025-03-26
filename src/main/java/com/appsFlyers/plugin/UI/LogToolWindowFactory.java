package com.appsFlyers.plugin.UI;

import com.appsFlyers.plugin.GetInfo;
import com.appsFlyers.plugin.LogcatProcessHandler;
import com.appsFlyers.plugin.actions.ClearLogsAction;
import com.appsFlyers.plugin.actions.RunAction;
import com.appsFlyers.plugin.showLogs;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.appsFlyers.plugin.UI.enterLogPanelUI.adjustTextAreaHeight;

// LogToolWindowFactory.java
public class LogToolWindowFactory implements ToolWindowFactory {

    private static JPanel logPanel;
    public static JComboBox<String> deviceCombo;


    private static @NotNull JComboBox<String> getStringJComboBox() {
        JComboBox<String> filterCombo = new JComboBox<>();
        filterCombo.addItem("All Logs");
        filterCombo.addItem("Conversion Logs");
        filterCombo.addItem("Event Logs");
        filterCombo.addItem("Launch Logs");
        filterCombo.addItem("Deep Link Logs");
        filterCombo.addItem("Error Logs");


        filterCombo.addActionListener(e -> {
            String selectedFilter = (String) filterCombo.getSelectedItem();
            if (selectedFilter == null) return;

            switch (selectedFilter) {
                case "All Logs":
                    showLogs.filterLogs(null); // Use null for "All Logs"
                    break;
                case "Conversion Logs":
                    showLogs.filterLogs("CONVERSION");
                    break;
                case "Event Logs":
                    showLogs.filterLogs("EVENT");
                    break;
                case "Launch Logs":
                    showLogs.filterLogs("LAUNCH");
                    break;
                case "Deep Link Logs":
                    showLogs.filterLogs("DEEPLINK");
                    break;
                case "Error Logs": // Handle Error Logs
                    showLogs.filterLogs("ERROR");
                    break;

            }
        });
        return filterCombo;
    }

    public static void loadDevices() {
        try {
            List<String> devices = GetInfo.getConnectedDevices(GetInfo.getAdbPath());
            deviceCombo.removeAllItems();
            if (devices.isEmpty()) {
                if (containsDevice(deviceCombo, "No devices")) {
                    deviceCombo.addItem("No devices");
                }
            } else {
                for (String device : devices) {
                    if (containsDevice(deviceCombo, device)) {
                        String displayName = GetInfo.getDeviceName(GetInfo.getAdbPath(), device);
                        deviceCombo.addItem(displayName + " (" + device + ")");
                    }
                }
            }
        } catch (IOException e) {
            deviceCombo.removeAllItems();
            deviceCombo.addItem("Error retrieving devices");
        }
    }

    private static boolean containsDevice(JComboBox<String> combo, String device) {
        ComboBoxModel<String> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (device.equals(model.getElementAt(i))) {
                return false;
            }
        }
        return true;
    }
}
