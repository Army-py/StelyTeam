package fr.army.stelyteam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.config.message.Placeholders;

public class PlaceholdersUtils {

    public static String replace(@NotNull String input, @NotNull Map<Placeholders, String> args) {
        for (Placeholders placeholder : args.keySet()) {
            input = input.replace("{" + placeholder.toString() + "}", args.get(placeholder));
        }

        return input;
    }

    public static String replace(@NotNull String input, @NotNull String... args) {
        for (int i = 0; i < args.length; i++) {
            input = input.replace("{" + i + "}", args[i]);
        }

        return input;
    }

    public static List<String> replaceList(@NotNull List<String> input, @NotNull Map<Placeholders, String> args) {
        List<String> output = new ArrayList<>();

        for (String line : input) {
            for (Placeholders placeholder : args.keySet()) {
                line = line.replace("{" + placeholder.toString() + "}", args.get(placeholder));
            }
            output.add(line);
        }

        return output;
    }

    public static List<String> replaceList(@NotNull List<String> input, @NotNull String... args) {
        List<String> output = new ArrayList<>();

        for (String line : input) {
            for (int i = 0; i < args.length; i++) {
                line = line.replace("{" + i + "}", args[i]);
            }
            output.add(line);
        }

        return output;
    }
}
