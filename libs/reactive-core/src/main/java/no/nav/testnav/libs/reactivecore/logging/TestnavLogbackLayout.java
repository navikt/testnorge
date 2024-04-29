package no.nav.testnav.libs.reactivecore.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestnavLogbackLayout extends PatternLayout {
    // matches exactly 11 digits (\\d{11}) that are not immediately preceded ((?<!\\d)) or followed ((?!\\d)) by another digit.
    private final Pattern pattern = Pattern.compile("(?<!\\d)\\d{11}(?!\\d)");

    public TestnavLogbackLayout() {
        this.setPattern("%-5relative %-5level %logger{35} - %msg%ex%n");
    }


    @Override
    public String doLayout(ILoggingEvent event) {
        String logEvent = event.toString();
        Matcher matcher = pattern.matcher(logEvent);

        if (!matcher.find()) {
            return event.getMessage();
        }

        matcher.reset(); // reset the matcher to the beginning
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String match = matcher.group();
            if (match.charAt(2) == '0' || match.charAt(2) == '1') {
                String replacement = match.substring(0, 6) + "xxxxx";
                matcher.appendReplacement(result, replacement);
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }
}