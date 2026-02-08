package peick.scathapro.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ScathaConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File file;
    private Map<String, Object> data = new HashMap<>();

    public ScathaConfig() {
        this.file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "scathapro.json");
        load();
    }

    public void load() {
        try {
            if (!file.exists()) {
                save();
                return;
            }

            FileReader reader = new FileReader(file);
            data = GSON.fromJson(reader, Map.class);
            reader.close();

            if (data == null) data = new HashMap<>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(file);
            GSON.toJson(data, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Boolean helpers
    public boolean getBoolean(String key) {
        Object value = data.get(key);
        return value instanceof Boolean ? (Boolean) value : false;
    }

    public void setBoolean(String key, boolean value) {
        data.put(key, value);
        save();
    }

    // Double helpers
    public double getDouble(String key) {
        Object value = data.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    public void setDouble(String key, double value) {
        data.put(key, value);
        save();
    }

    // Default values
    public void ensureDefaults() {
        putDefault("overlayEnabled", true);
        putDefault("showChain", true);
        putDefault("showTimer", true);
        putDefault("showChance", true);

        putDefault("overlayScale", 1.0);
        putDefault("overlayOpacity", 1.0);
    }

    private void putDefault(String key, Object value) {
        if (!data.containsKey(key)) {
            data.put(key, value);
            save();
        }
    }
}