package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Record {
    private int record = 0;
    private StringBuilder builder = new StringBuilder();

    private static String getDateNowFormatted() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    public void append(String value) {
        record++;
        builder.append(value);
    }

    public int getRecord() {
        return record;
    }

    public String build() {
        return builder.toString();
    }
}
