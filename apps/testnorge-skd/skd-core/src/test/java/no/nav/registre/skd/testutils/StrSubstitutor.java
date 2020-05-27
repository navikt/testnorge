package no.nav.registre.skd.testutils;

import java.util.Map;
import java.util.regex.Pattern;

public class StrSubstitutor {

    private static final Pattern p = Pattern.compile("\\$\\{(.+?)}");

    public static String replace(String text, Map<String, String> placeholderValues) {
        var m = p.matcher(text);
        var sb = new StringBuffer();
        while (m.find()) {
            var var = m.group(1);
            var replacement = placeholderValues.get(var);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
