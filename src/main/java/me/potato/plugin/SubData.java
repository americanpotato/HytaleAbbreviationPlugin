package me.potato.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// holds data of a single substitution
// also helper functions to interact with the substitutions data
public class SubData {
    // keep index identifier because abbreviations can happen with different parameters
    private int index;
    private int parameters;
    private String sub;

    // holds original command
    // or multiple original commands
    private String original;

    public SubData(String sub, String original, int index) {
        this.parameters = parameters;
        this.sub = sub;
        this.original = original;
        this.index = index;

        this.parameters = countParams(original);
    }

    public String toJSON() {
        return "[\"" + sub + "\",\"" + original + "\"]";
    }

    // takes in something like "xyz 100 330 22"
    public String getExecuteString(String userInput) {
        if (this.sub == null || this.sub.isEmpty()
                || this.original == null || this.original.isEmpty()) {
            return "";
        }

        String[] subParts = this.sub.split("\\s+");
        String[] inputParts = userInput.split("\\s+");

        if (subParts.length != inputParts.length) {
            throw new IllegalArgumentException("Input does not match the expected pattern.");
        }

        boolean usesPositional = false;
        boolean usesNamed = false;

        for (String token : subParts) {
            if (token.equals("$")) usesPositional = true;
            else if (token.startsWith("$")) usesNamed = true;
        }

        if (usesPositional && usesNamed) {
            throw new IllegalArgumentException("Cannot mix positional '$' and named placeholders.");
        }

        // ===== MODE 1: POSITIONAL =====
        if (usesPositional) {
            List<String> values = new ArrayList<>();

            for (int i = 0; i < subParts.length; i++) {
                String subToken = subParts[i];
                String inputToken = inputParts[i];

                if (subToken.equals("$")) {
                    values.add(inputToken);
                } else {
                    if (!subToken.substring(1).equals(inputToken)) {
                        throw new IllegalArgumentException(
                                "Input token '" + inputToken +
                                        "' does not match expected literal '" + subToken + "'"
                        );
                    }
                }
            }

            String result = this.original;

            for (String value : values) {
                int idx = result.indexOf("$");
                if (idx == -1) {
                    throw new IllegalArgumentException("Not enough '$' placeholders in original.");
                }
                result = result.substring(0, idx) + value + result.substring(idx + 1);
            }

            if (result.contains("$")) {
                throw new IllegalArgumentException("Too many '$' placeholders in original.");
            }

            return result.stripTrailing().substring(1);
        }

        // ===== MODE 2: NAMED =====
        Map<String, String> placeholderMap = new HashMap<>();

        for (int i = 0; i < subParts.length; i++) {
            String subToken = subParts[i];
            String inputToken = inputParts[i];

            if (subToken.startsWith("$")) {
                placeholderMap.put(subToken, inputToken);
            } else {
                if (!subToken.substring(1).equals(inputToken)) { // substring to remove /
                    throw new IllegalArgumentException(
                            "Input token '" + inputToken +
                                    "' does not match expected literal '" + subToken + "'"
                    );
                }
            }
        }

        String result = this.original;

        for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        if (result.matches(".*\\$[A-Za-z0-9]+.*")) {
            throw new IllegalArgumentException("Original contains undefined placeholders.");
        }

        return result.stripTrailing().substring(1); // substring to remove /
    }




    public void setIndex(int index) {
        this.index = index;
    }

    public String getSub() {
        return this.sub;
    }

    public String getOriginal() {
        return this.original;
    }

    public int getParamCount() {
        return this.parameters;
    }

    public static int countParams(String cmd) {
        return (int) cmd.chars().filter(c -> c == '$').count();
    }
}
