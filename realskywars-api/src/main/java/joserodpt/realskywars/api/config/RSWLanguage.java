package joserodpt.realskywars.api.config;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class RSWLanguage {

    private final String name;
    private final String displayName;
    private final String translationKey;
    private final ItemStack icon;

    private final File folder = new File(RealSkywarsAPI.getInstance().getPlugin().getDataFolder(), "translations");
    private final File file;
    private JsonObject json = null;

    private final String version = RealSkywarsAPI.getInstance().getSimpleServerVersion();

    public RSWLanguage(String name, String displayName, String translationKey, ItemStack icon) {
        this.name = name;
        this.displayName = displayName;
        this.translationKey = translationKey;
        this.icon = icon;
        file = new File(folder, translationKey + ".json");

        // download the language file from https://assets.mcasset.cloud/1.21/assets/minecraft/lang/{getTranslationKey()}.json to the translations folder
        if (folder.exists() && !file.exists()) {
            try {
                downloadLanguageFile();
            } catch (Exception e) {
                Debugger.print(RSWLanguage.class, "Could not load language " + this.name + " - " + this.displayName + " -> Exception: " + e.getMessage());
            }
        }
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void downloadLanguageFile() throws Exception {
        RealSkywarsAPI.getInstance().getLogger().info("Downloading minecraft language file for " + getTranslationKey() + " (" + version + ") ...");

        String fileName = getTranslationKey() + ".json";
        String urlString = "https://assets.mcasset.cloud/" + version + "/assets/minecraft/lang/" + fileName;

        try {
            // Ensure the translations folder exists
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Open a connection to the URL
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check if the response code is OK (200)
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream from the connection
                try (InputStream inputStream = connection.getInputStream()) {
                    // Write the input stream to the file
                    Files.copy(inputStream, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                RealSkywarsAPI.getInstance().getLogger().severe("Failed to download language file for " + getTranslationKey() + "(" + version + ") -> Response: " + connection.getResponseMessage());
            }

            // Disconnect the connection
            connection.disconnect();

            RealSkywarsAPI.getInstance().getLogger().info("Downloaded minecraft language file for " + getTranslationKey() + " (" + version + ")!");
        } catch (IOException e) {
            // RealSkywarsAPI.getInstance().getLogger().severe("An error occurred while downloading the language file: " + e.getMessage());
            throw e;
        }
    }

    private void loadJsonFile() throws Exception {
        // download the language file from https://assets.mcasset.cloud/1.21/assets/minecraft/lang/{getTranslationKey()}.json to the translations folder
        if (folder.exists() && !file.exists()) {
            downloadLanguageFile();
        }

        if (json != null) {
            return;
        }

        parseJsonFile(file);
    }

    private void parseJsonFile(File file) throws IOException {
        if (file.exists()) {
            try (InputStreamReader streamReader = new InputStreamReader(Files.newInputStream(file.toPath()));
                 BufferedReader reader = new BufferedReader(streamReader)) {
                Gson gson = new Gson();
                this.json = gson.fromJson(reader, JsonObject.class);
            }
        }
    }

    //available data from minecraft language files

    public String getMaterialName(Material mat) {
        try {
            loadJsonFile();
        } catch (Exception e) {
            Debugger.print(RSWLanguage.class, "Could not load language " + this.name + " - " + this.displayName + " -> Exception: " + e.getMessage());
            return Text.beautifyEnumName(mat.name());
        }

        String name = mat.getKey().getKey();
        if (name.contains("wall_")) name = name.replace("wall_", "");

        return getString((mat.isBlock() ? "block.minecraft." : "item.minecraft.") + name);
    }

    public String getEnchantmentName(Enchantment ench) {
        try {
            loadJsonFile();
        } catch (Exception e) {
            Debugger.print(RSWLanguage.class, "Could not load language " + this.name + " - " + this.displayName + " -> Exception: " + e.getMessage());
            return Text.beautifyEnumName(ench.getKey().getKey());
        }

        return getString("enchantment.minecraft." + ench.getKey().getKey());
    }

    public String getEntityName(EntityType type) {
        try {
            loadJsonFile();
        } catch (Exception e) {
            Debugger.print(RSWLanguage.class, "Could not load language " + this.name + " - " + this.displayName + " -> Exception: " + e.getMessage());
            return Text.beautifyEnumName(type.name());
        }

        String name = type.name();
        if (name == null) return getString("entity.notFound");
        return getString("entity.minecraft." + name.toLowerCase());
    }

    private String getString(String key) {
        try {
            loadJsonFile();
        } catch (Exception e) {
            Debugger.print(RSWLanguage.class, "Could not load language " + this.name + " - " + this.displayName + " -> Exception: " + e.getMessage());
            return key;
        }

        if (json == null) return "Language file " + getTranslationKey() + " not loaded!";
        return json.get(key).getAsString();
    }
}
