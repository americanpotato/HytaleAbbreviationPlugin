package me.potato.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandConfig {

    private static final String CONFIG_FILE = "./config/cmdsubstitutions.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Type CONFIG_TYPE =
            new TypeToken<Map<String, List<List<String>>>>() {}.getType();

    private static List<SubData> mappings = new ArrayList<>();

    public CommandConfig() {
        loadConfig();
    }

    /* -------------------- LOAD -------------------- */

    private void loadConfig() {
        File file = new File(CONFIG_FILE);

        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                InputStream in = getClass()
                        .getClassLoader()
                        .getResourceAsStream("cmdsubstitutions.json");

                if (in != null) {
                    Files.copy(in, file.toPath());
                } else {
                    saveConfig(); // create empty config
                    return;
                }
            }

            Reader reader = new FileReader(file);
            Map<String, List<List<String>>> data =
                    GSON.fromJson(reader, CONFIG_TYPE);
            reader.close();

            mappings.clear();

            List<List<String>> commands = data.get("commands");
            if (commands != null) {
                int index = 0;
                for (List<String> pair : commands) {
                    if (pair.size() == 2) {
                        mappings.add(new SubData(
                                pair.get(0),
                                pair.get(1),
                                index++
                        ));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* -------------------- SAVE -------------------- */

    private void saveConfig() {
        try {
            File file = new File(CONFIG_FILE);
            file.getParentFile().mkdirs();

            Map<String, List<List<String>>> data = new HashMap<>();
            List<List<String>> commands = new ArrayList<>();

            for (SubData sub : mappings) {
                List<String> pair = new ArrayList<>();
                pair.add(sub.getSub());
                pair.add(sub.getOriginal());
                commands.add(pair);
            }

            data.put("commands", commands);

            Writer writer = new FileWriter(file);
            GSON.toJson(data, writer);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* -------------------- ADD -------------------- */

    public SubData addToConfig(String abbreviation, String originalCommand) {
        SubData sub = new SubData(
                abbreviation,
                originalCommand,
                mappings.size()
        );

        mappings.add(sub);
        saveConfig();
        return sub;
    }

    /* -------------------- REMOVE -------------------- */

    public boolean removeSub(int index) {
        if (index < 0 || index >= mappings.size()) {
            return false;
        }

        mappings.remove(index);

        // re-index
        for (int i = 0; i < mappings.size(); i++) {
            mappings.get(i).setIndex(i);
        }

        saveConfig();
        return true;
    }

    /* -------------------- QUERY -------------------- */

    public List<SubData> getMappings() {
        return mappings;
    }
}
