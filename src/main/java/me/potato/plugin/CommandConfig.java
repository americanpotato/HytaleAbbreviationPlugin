package me.potato.plugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CommandConfig {
    private static final String CONFIG_FILE = "./config/cmdsubstitutions.json";
    private static List<List<String>> mappings;

    public CommandConfig() {
        loadConfig();
    }

    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        try {
            // Ensure config directory exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (!file.exists()) {
                // Copy default from resources
                InputStream in = getClass().getClassLoader().getResourceAsStream("cmdsubstitutions.json");
                Files.copy(in, file.toPath());
            }

            Reader reader = new FileReader(file);
            Gson gson = new Gson();
            Type type = new TypeToken<CommandWrapper>() {}.getType();
            CommandWrapper wrapper = gson.fromJson(reader, type);
            reader.close();

            mappings = wrapper.commands != null ? wrapper.commands : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            mappings = new ArrayList<>();
        }
    }

    public List<String> addToConfig(String abbreviation, String originalCommand) {
        // Add new mapping
        List<String> mapping = new ArrayList<>();
        mapping.add(abbreviation);
        mapping.add(originalCommand);
        mappings.add(mapping);

        // Save back to JSON file
        try {
            File file = new File(CONFIG_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Ensure parent directories exist
                file.createNewFile();
            }

            // Wrap the mappings into CommandWrapper for JSON
            CommandWrapper wrapper = new CommandWrapper();
            wrapper.commands = mappings;

            Gson gson = new Gson();
            Writer writer = new FileWriter(file);
            gson.toJson(wrapper, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapping;
    }

    public static List<String> SubAndArgCountToMapping(SubAndArgCount and) {
        for(List<String> list : mappings) {
            if(and.subBase.equals(list.getFirst().split(" ")[0]) && and.expectedArgs == list.getLast().chars()
                    .filter(ch -> ch == '$')
                    .count()) {
                return list;
            }
        }
        return null;
    }

    public boolean removeSub(int index) {
        int listIndex = index;

        if (listIndex < 0 || listIndex >= mappings.size()) {
            return false; // invalid index
        }

        mappings.remove(listIndex);

        // Save updated mappings back to JSON
        try {
            File file = new File(CONFIG_FILE);

            CommandWrapper wrapper = new CommandWrapper();
            wrapper.commands = mappings;

            Gson gson = new Gson();
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(wrapper, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<List<String>> getMappings() {
        return mappings;
    }

    private static class CommandWrapper {
        List<List<String>> commands;
    }
}
