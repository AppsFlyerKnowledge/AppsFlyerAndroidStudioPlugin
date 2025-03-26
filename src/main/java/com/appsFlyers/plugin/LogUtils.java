package com.appsFlyers.plugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class LogUtils {

    // method to extract needed info from logs
    public static String extractMessageFromJson(String type, String logText) {
        try {
            // Special handling for "No deep link detected" message
            if (type.equals("DEEPLINK") && logText.contains("No deep link")) {
                return logText;
            }

            int jsonStartIndex = logText.indexOf("{");
            int jsonEndIndex = logText.length()-1;

            // Check if indices are valid
            if (jsonStartIndex == -1 || jsonEndIndex == -1 || jsonEndIndex < jsonStartIndex) {
                System.err.println("Error: JSON not found in log text.");
                return null;
            }

            // Check if indices are within string bounds
            if (jsonEndIndex + 1 > logText.length()) {
                System.err.println("Error: Invalid substring range.");
                return null;
            }

            String jsonPart = logText.substring(jsonStartIndex, jsonEndIndex).trim();

}