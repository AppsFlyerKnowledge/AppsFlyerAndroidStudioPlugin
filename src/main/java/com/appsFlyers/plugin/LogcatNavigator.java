package com.appsFlyers.plugin;

// Instead of reflection, try using the official API
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;


public class LogcatNavigator {
    /// Navigates to the Android Studio Logcat window and filters for a specific timestamp
    /// @param timestamp The timestamp to search for in Logcat
    Logger logger = Logger.getInstance(LogcatNavigator.class);

    public static void navigateToLogcatEntry(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return;
        }

        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length == 0) {
            return;
        }

        // Use the current active project
        Project project = openProjects[0];

        // Schedule on EDT to avoid threading issues
        SwingUtilities.invokeLater(() -> {
            // Find and focus the Logcat tool window
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            ToolWindow logcatWindow = toolWindowManager.getToolWindow("Logcat");

            if (logcatWindow != null) {
                // Activate the Logcat tool window
                logcatWindow.show(() -> {
                    // After window is shown, apply filter to find the specific log
                    try {
                        applyLogcatFilter( timestamp, logcatWindow);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    /**
     * Applies a filter to the Logcat window to find logs with a specific timestamp
     * @param logcatWindow The current project
     * @param timestamp The timestamp to filter for
     */
    private static void applyLogcatFilter(String timestamp, ToolWindow logcatWindow ) throws NoSuchMethodException {
        ContentManager contentManager = logcatWindow.getContentManager();
        Content[] contents = contentManager.getContents();

        for (Content content : contents) {
            // Find the FilterTextField component by its class name
            Component filterTextField = findComponentByClassName(
                    content.getComponent(),
                    "com.android.tools.idea.logcat.filters.FilterTextField"
            );

            if (filterTextField != null) {
                // Try to find the FilterEditorTextField within it
                Component editorTextField = findComponentByClassName(
                        filterTextField,
                        "com.android.tools.idea.logcat.filters.FilterTextField$FilterEditorTextField"
                );

                if (editorTextField != null) {
                    // Try to access the editor component directly or via reflection
                    try {
                        // Option 1: Try direct method if available
                        Method setTextMethod = editorTextField.getClass().getMethod("setText", String.class);
                        try {
                            setTextMethod.invoke(editorTextField, timestamp);
                            return; // אם הצלחנו, לא ממשיכים לשיטות אחרות
                        } catch (Exception e) {
                            System.out.println("Could not set text directly: " + e.getMessage());
                        }
                    } catch (NoSuchMethodException | SecurityException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    private static Component findComponentByClassName (Component component, String className){
        if (component == null) return null;
        // Check if this component matches the class name
        if (component.getClass().getName().equals(className)) {
            return component;
        }
        // Search in child components
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                Component result = findComponentByClassName(child, className);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
