package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Flatfil {

    private final List<Record> records = new ArrayList<>();

    public static Flatfil create(List<Record> records) {
        Flatfil flatfil = new Flatfil();
        records.forEach(flatfil::add);
        return flatfil;
    }

    private static String formattedDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return localDate.format(formatter);
    }

    private static String getDateNowFormatted() {
        return formattedDate(LocalDate.now());
    }

    private static StringBuilder createStringBuilderWithReplacement(int size, char replacement) {
        StringBuilder stringBuilder = new StringBuilder(size);
        stringBuilder.setLength(size);
        for (int i = 0; i < stringBuilder.length(); i++) {
            stringBuilder.setCharAt(i, replacement);
        }
        return stringBuilder;
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
