package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.Objects.nonNull;

public class Record {
    private final StringBuilder builder = new StringBuilder();
    private int record = 0;

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

    public static Record create(List<Line> lines, String orgnummer, String enhetstype, Date regDato, boolean update) {
        Record record = new Record();
        record.append(createEHN(update, orgnummer, enhetstype, regDato));
        lines.forEach(line -> record.append(line.getValue()));
        return record;
    }

    private static String createEHN(boolean update, String orgnummer, String enhetstype, Date regDato) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(49, ' ');

        String dateNowFormatted = getDateNowFormatted(regDato);
        String undersakstype = update ? "EN" : "NY";

        stringBuilder.replace(0, "ENH".length(), "ENH")
                .replace(4, 4 + orgnummer.length(), orgnummer)
                .replace(13, 13 + enhetstype.length(), enhetstype)
                .replace(17, 18, update ? "E" : "N")
                .replace(18, 18 + undersakstype.length(), undersakstype)
                .replace(22, 22 + (dateNowFormatted + dateNowFormatted).length(), dateNowFormatted + dateNowFormatted + "J")
                .append("\n");

        return stringBuilder.toString();
    }

    private static StringBuilder createStringBuilderWithReplacement(int size, char replacement) {
        StringBuilder stringBuilder = new StringBuilder(size);
        stringBuilder.setLength(size);
        for (int i = 0; i < stringBuilder.length(); i++) {
            stringBuilder.setCharAt(i, replacement);
        }
        return stringBuilder;
    }

    private static String getDateNowFormatted(Date regDato) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(nonNull(regDato) ? regDato : new Date());
    }
}
