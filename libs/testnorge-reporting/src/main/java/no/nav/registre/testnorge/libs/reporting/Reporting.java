package no.nav.registre.testnorge.libs.reporting;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import no.nav.testnav.libs.dto.rapprtering.v1.EntryStatus;
import no.nav.registre.testnorge.libs.reporting.domin.Config;
import no.nav.registre.testnorge.libs.reporting.domin.Entry;
import no.nav.registre.testnorge.libs.reporting.domin.Report;

public class Reporting {

    private final Config config;
    private LocalDateTime start;
    private LocalDateTime end;
    private final List<Entry> entries;

    Reporting(Config config) {
        this.config = config;
        this.entries = new ArrayList<>();
    }

    void start() {
        start = LocalDateTime.now();
    }

    public void info(String entry, Object... values) {
        report(EntryStatus.INFO, entry, values);
    }

    public void warn(String entry, Object... values) {
        report(EntryStatus.WARNING, entry, values);
    }

    public void error(String entry, Object... values) {
        report(EntryStatus.ERROR, entry, values);
    }

    private void report(EntryStatus status, String entry, Object... values) {
        var message = values == null || values.length == 0 ? entry : Arrays
                .stream(values)
                .map(Object::toString)
                .reduce(entry, (pre, current) -> pre.replaceFirst("\\{\\}", Matcher.quoteReplacement(current)));
        entries.add(new Entry(status, message, LocalDateTime.now()));
    }

    void end() {
        end = LocalDateTime.now();
    }

    Report build() {
        return new Report(config.getName(), start, end, entries);
    }
}