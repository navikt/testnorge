package no.nav.testnav.libs.reactivecore.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestnavLogbackEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    private final Pattern pattern = Pattern.compile("(?<!\\d)\\d{11}(?!\\d)");

    @Override
    public void start() {
        TestnavPatternLayout patternLayout = new TestnavPatternLayout();
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

    private class TestnavPatternLayout extends PatternLayout {
        @Override
        protected String writeLoopOnConverters(ILoggingEvent event) {
            String logEvent = super.writeLoopOnConverters(event);
            Matcher matcher = pattern.matcher(logEvent);

            if (!matcher.find()) {
                return logEvent;
            }

            matcher.reset();
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
}