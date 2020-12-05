package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Flatfil {
    private List<Record> records = new ArrayList<>();

    private static String getDateNowFormatted() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    private String createHeader() {
        return "HEADER " + getDateNowFormatted() + "00000" + "AA A\n";
    }

    private String createTrailer(int units, int records) {
        records = records + 2;
        StringBuilder stringBuilder = createStringBuilderWithReplacement(23, '0');
        stringBuilder.replace(0, 6, "TRAIER ")
                .replace(14 - String.valueOf(units).length(), 14, String.valueOf(units))
                .replace(23 - String.valueOf(records).length(), 24, String.valueOf(records))
                .append("\n");
        return stringBuilder.toString();
    }

    private StringBuilder createStringBuilderWithReplacement(int size, char replacement) {
        StringBuilder stringBuilder = new StringBuilder(size);
        stringBuilder.setLength(size);
        for (int i = 0; i < stringBuilder.length(); i++) {
            stringBuilder.setCharAt(i, replacement);
        }
        return stringBuilder;
    }

    public void add(Record record) {
        records.add(record);
    }

    public String build() {
        Integer count = records.stream().map(Record::getRecord).reduce(0, Integer::sum);
        StringBuilder builder = new StringBuilder();
        builder.append(createHeader());
        records.forEach(record -> builder.append(record.build()));
        builder.append(createTrailer(records.size(), count));
        return builder.toString();
    }
}
