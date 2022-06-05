package fr.army.stelyteam.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class ColorsBuilder {
    public String replaceColor(final String input) {
        final StringBuffer legacyBuilder = new StringBuffer();
        final Pattern allPattern = Pattern.compile("(&)?&([0-9a-fk-orA-FK-OR])");
        final Matcher legacyMatcher = allPattern.matcher(input);
        legacyLoop:
        while (legacyMatcher.find()) {
            final boolean isEscaped = legacyMatcher.group(1) != null;
            if (!isEscaped) {
                final char code = legacyMatcher.group(2).toLowerCase(Locale.ROOT).charAt(0);
                for (final ChatColor color : ChatColor.values()) {
                    if (color.getChar() == code) {
                        legacyMatcher.appendReplacement(legacyBuilder, ChatColor.COLOR_CHAR + "$2");
                        continue legacyLoop;
                    }
                }
            }
            legacyMatcher.appendReplacement(legacyBuilder, "&$2");
        }
        legacyMatcher.appendTail(legacyBuilder);

        final StringBuffer rgbBuilder = new StringBuffer();
        Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");
        final Matcher rgbMatcher = hexPattern.matcher(legacyBuilder.toString());
        while (rgbMatcher.find()) {
            System.out.println(rgbMatcher.group());
            final String hexCode = rgbMatcher.group().replace("&#", "");
            rgbMatcher.appendReplacement(rgbBuilder, parseHexColor(hexCode));
            System.out.println("tt");
        }
        rgbMatcher.appendTail(rgbBuilder);
        return rgbBuilder.toString();
    }


    public static String parseHexColor(String hexColor) {
        Color.fromRGB(Integer.decode("#" + hexColor));
        final StringBuilder assembledColorCode = new StringBuilder();
        assembledColorCode.append(ChatColor.COLOR_CHAR + "x");
        for (final char curChar : hexColor.toCharArray()) {
            assembledColorCode.append(ChatColor.COLOR_CHAR).append(curChar);
        }
        return assembledColorCode.toString();
    }
}
