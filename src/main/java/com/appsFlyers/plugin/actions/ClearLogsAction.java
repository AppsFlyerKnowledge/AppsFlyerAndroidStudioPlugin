package com.appsFlyers.plugin.actions;

import com.appsFlyers.plugin.UI.LogToolWindowFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import javax.swing.Icon;

public class ClearLogsAction extends AnAction {
    public static class MyClass {
        public static final Icon CLEAR_ICON = IconLoader.getIcon("AllIcons.Actions.GC", MyClass.class);
    }

    public ClearLogsAction() {
        super("Clear Logs", "Clear the logs panel", MyClass.CLEAR_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LogToolWindowFactory.clearLogs();
    }
}