package com.appsFlyers.plugin.UI;

import com.appsFlyers.plugin.LogcatNavigator;
import com.appsFlyers.plugin.actions.LogIconUtils;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.Gray;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;

public class enterLogPanelUI {
    private static final int MIN_PANEL_HEIGHT = 60;

    public static @NotNull JPanel createLogEntryPanel(String log, String fullLog) {
        RoundedPanel entryPanel = new RoundedPanel(12, Gray._60);
        entryPanel.setLayout(new BorderLayout());
        entryPanel.setBorder(JBUI.Borders.empty(8, 10));
        entryPanel.setMinimumSize(new Dimension(100, MIN_PANEL_HEIGHT));

        JTextArea logLabel = new JTextArea(log);
        logLabel.setLineWrap(true);
        logLabel.setWrapStyleWord(true);
        logLabel.setEditable(false);
        logLabel.setOpaque(false);
        logLabel.setFont(logLabel.getFont().deriveFont(Font.PLAIN, 12f));
        logLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        adjustTextAreaHeight(logLabel, entryPanel);

        final boolean[] isShowingLog = {true};

        JLabel iconLabel = LogIconUtils.getLogIconLabel(log);
        iconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel optionsLabel = new JLabel(IconLoader.getIcon("AllIcons.Actions.More", enterLogPanelUI.class));
        optionsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem toggleItem = new JMenuItem("Original log");
        toggleItem.addActionListener(e -> {
            if (isShowingLog[0]) {
                logLabel.setText(fullLog);
            } else {
                logLabel.setText(log);
            }
            isShowingLog[0] = !isShowingLog[0];
            SwingUtilities.invokeLater(() -> {
                adjustTextAreaHeight(logLabel, entryPanel);
                entryPanel.invalidate();
                Container parent = entryPanel.getParent();
                if (parent != null) {
                    parent.validate();
                    parent.repaint();
                }
            });
        });
        popupMenu.add(toggleItem);

        JMenuItem showInLogcatItem = new JMenuItem("Show in Logcat");
        String timestamp = extractTimestamp(log);
        showInLogcatItem.addActionListener(e -> showInLogcat(timestamp));
        popupMenu.add(showInLogcatItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(e -> {
            if (isShowingLog[0]) {
                copyToClipboard(log);
            } else {
                StringSelection selection = new StringSelection(fullLog);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
            }
        });
        popupMenu.add(copyItem);

        JMenuItem copyTimestampItem = new JMenuItem("Copy Timestamp");
        copyTimestampItem.addActionListener(e -> {
            StringSelection selection = new StringSelection(timestamp);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        });
        popupMenu.add(copyTimestampItem);

        optionsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.show(optionsLabel, e.getX(), e.getY());
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(iconLabel, BorderLayout.WEST);
        wrapper.add(optionsLabel, BorderLayout.EAST);
        wrapper.add(logLabel, BorderLayout.CENTER);

        entryPanel.add(wrapper, BorderLayout.CENTER);
        entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        entryPanel.setPreferredSize(new Dimension(entryPanel.getPreferredSize().width,
                Math.max(MIN_PANEL_HEIGHT, logLabel.getPreferredSize().height + 16)));

        entryPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustTextAreaHeight(logLabel, entryPanel);
            }
        });

        return entryPanel;
    }

    static void adjustTextAreaHeight(JTextArea textArea, JPanel globalPanel) {
        int width = textArea.getWidth();
        if (width > 0) {
            View view = textArea.getUI().getRootView(textArea);
            view.setSize(width, Integer.MAX_VALUE);
            float preferredHeight = view.getPreferredSpan(View.Y_AXIS);
            textArea.setPreferredSize(new Dimension(width, (int) preferredHeight));
            textArea.revalidate();
            globalPanel.setPreferredSize(new Dimension(globalPanel.getWidth(), (int) preferredHeight + 16));
            globalPanel.revalidate();
        }
    }

    private static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private static String extractTimestamp(String log) {
        if (log != null && log.length() > 18) {
            return log.substring(0, 18).trim();
        }
        return null;
    }

    private static void showInLogcat(String timestamp) {
        LogcatNavigator.navigateToLogcatEntry(timestamp);
    }
}