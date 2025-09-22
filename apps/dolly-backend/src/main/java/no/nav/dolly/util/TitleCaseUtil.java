package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class TitleCaseUtil {

    public static String toTitleCase(String input) {

        if (isNull(input) || input.isEmpty()) {
            return input;
        }

        var titleCaseBuilder = new StringBuilder();
        var capitalizeNext = true; // Flag to indicate if the next character should be capitalized

        for (char ch : input.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                titleCaseBuilder.append(ch);
                capitalizeNext = true; // Capitalize the first letter of the next word
            } else if (capitalizeNext) {
                titleCaseBuilder.append(Character.toUpperCase(ch));
                capitalizeNext = false;
            } else {
                titleCaseBuilder.append(Character.toLowerCase(ch));
            }
        }
        return titleCaseBuilder.toString();
    }
}
