package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Record {
    private final StringBuilder builder = new StringBuilder();
    private int record = 0;

    public static Record create(List<Line> lines, String orgnummer, String enhetstype, boolean update) {
        Record record = new Record();
        record.append(createEHN(update, orgnummer, enhetstype));
        lines.forEach(line -> record.append(line.getValue()));
        return record;
    }

    private static String createEHN(boolean update, String orgnummer, String enhetstype) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(49, ' ');

        String dateNowFormatted = getDateNowFormatted();
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
