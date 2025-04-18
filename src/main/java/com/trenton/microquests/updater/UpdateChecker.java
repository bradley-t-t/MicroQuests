package com.trenton.microquests.updater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trenton.microquests.MicroQuests;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class UpdateChecker {

    private final MicroQuests plugin;
    private final int resourceId;
    private String latestVersion;
    private boolean updateAvailable;

    public UpdateChecker(MicroQuests plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.updateAvailable = false;
    }

    public void checkForUpdates(boolean autoUpdate) {
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/" + resourceId + "/versions/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "MicroQuests-UpdateChecker");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                plugin.getLogger().warning("Failed to check for updates: HTTP " + responseCode + " for resource ID " + resourceId);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            if (!json.has("name")) {
                plugin.getLogger().warning("Spiget API response missing 'name' field: " + response);
                return;
            }
            latestVersion = json.get("name").getAsString();
            String currentVersion = plugin.getDescription().getVersion();

            if (isVersionNewer(latestVersion, currentVersion)) {
                updateAvailable = true;
                plugin.getLogger().info("Update available: v" + latestVersion + " (current: v" + currentVersion + ")");
                if (autoUpdate) {
                    downloadUpdate();
                }
            } else {
                plugin.getLogger().info("No update available. Current version: v" + currentVersion);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
        }
    }

    private boolean isVersionNewer(String latest, String current) {
        try {
            String[] latestParts = latest.replace("-SNAPSHOT", "").split("\\.");
            String[] currentParts = current.replace("-SNAPSHOT", "").split("\\.");
            int maxLength = Math.max(latestParts.length, currentParts.length);
            for (int i = 0; i < maxLength; i++) {
                int latestNum = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
                int currentNum = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                if (latestNum > currentNum) return true;
                if (latestNum < currentNum) return false;
            }
            return latest.contains("-SNAPSHOT") ? false : !current.contains("-SNAPSHOT");
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid version format: latest=" + latest + ", current=" + current);
            return false;
        }
    }

    private void downloadUpdate() {
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/" + resourceId + "/download");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "MicroQuests-UpdateChecker");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                plugin.getLogger().warning("Failed to download update: HTTP " + responseCode + " for resource ID " + resourceId);
                return;
            }

            File updateFolder = new File(plugin.getDataFolder(), "AutoUpdater");
            if (!updateFolder.exists()) {
                updateFolder.mkdirs();
            }
            File outputFile = new File(updateFolder, "MicroQuests-" + latestVersion + ".jar");

            ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
            FileOutputStream out = new FileOutputStream(outputFile);
            out.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            out.close();
            connection.disconnect();

            plugin.getLogger().info("Update downloaded to: " + outputFile.getPath() + ". Restart server to apply.");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to download update: " + e.getMessage());
        }
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
}