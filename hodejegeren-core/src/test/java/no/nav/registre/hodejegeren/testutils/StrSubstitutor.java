package no.nav.registre.hodejegeren.testutils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrSubstitutor {

    private static final Pattern p = Pattern.compile("\\$\\{(.+?)\\}");

    public static String replace(String text, Map<String, String> placeholderValues) {
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String var = m.group(1);
            String replacement = placeholderValues.get(var);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
