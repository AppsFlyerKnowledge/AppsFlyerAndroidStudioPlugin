package com.appsFlyers.plugin.actions;

import com.appsFlyers.plugin.LogcatProcessHandler;
import com.appsFlyers.plugin.UI.LogToolWindowFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import javax.swing.Icon;
import com.intellij.openapi.util.IconLoader;
import static com.appsFlyers.plugin.UI.LogToolWindowFactory.deviceCombo;

/**
 * Action that starts the logcat process and maintains the selected device.
 */
public class RunAction extends AnAction {
    public static class MyClass {
        public static final Icon MY_ICON = IconLoader.getIcon("AllIcons.Actions.Execute", MyClass.class);
    }

    public RunAction() {
        super("RUN", "Run action", MyClass.MY_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Store selected device index to restore it after loading devices
        int selectedIndex = deviceCombo.getSelectedIndex();
        LogToolWindowFactory.loadDevices();
        deviceCombo.setSelectedIndex(selectedIndex);
        LogcatProcessHandler.startLogcat();
    }
}