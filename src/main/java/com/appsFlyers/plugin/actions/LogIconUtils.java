package com.appsFlyers.plugin.actions;

    import com.intellij.openapi.util.IconLoader;
    import javax.swing.JLabel;

    public class LogIconUtils {

        public static JLabel getLogIconLabel(String log) {
            JLabel iconLabel;
            if (log.contains("ERROR") || log.contains("FAILURE") || log.contains("No deep link")) {
                iconLabel = new JLabel(IconLoader.getIcon("AllIcons.Actions.QuickfixBulb", LogIconUtils.class));
                if(log.contains("FAILURE")){
                    iconLabel.setToolTipText("check your DevKey");
                }
            } else if (log.contains("{") && log.contains("}")) {
                iconLabel = new JLabel(IconLoader.getIcon("AllIcons.FileTypes.Json", LogIconUtils.class));
            } else {
                iconLabel = new JLabel(IconLoader.getIcon("AllIcons.General.BalloonInformation", LogIconUtils.class));
            }
            return iconLabel;
        }
    }