package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class ToLine {
    String getDateFormatted(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    abstract FlatfilValueBuilder builder();

    public final Line toLine() {
        return Line
                .builder()
                .value(builder().toString())
                .build();
    }
}
