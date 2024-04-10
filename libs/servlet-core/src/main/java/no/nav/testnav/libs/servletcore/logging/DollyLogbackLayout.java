package no.nav.testnav.libs.servletcore.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DollyLogbackLayout extends PatternLayout {
    // matches exactly 11 digits (\\d{11}) that are not immediately preceded ((?<!\\d)) or followed ((?!\\d)) by another digit.
    private final Pattern pattern = Pattern.compile("(?<!\\\\d)\\\\d{11}(?!\\\\d)");

    public DollyLogbackLayout() {
        // Set a default pattern
        this.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        Matcher matcher = pattern.matcher(message);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String match = matcher.group();
            if (match.charAt(2) == '0' || match.charAt(2) == '1') {
                String replacement = match.substring(0, 6) + "xxxxx";
                matcher.appendReplacement(result, replacement);
            }
        }
        matcher.appendTail(result);

        return result + "\n";
    }
}