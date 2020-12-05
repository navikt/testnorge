package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public abstract class ToFlatfil {
    private final String orgnummer;
    private final String enhetstype;

    public ToFlatfil(Metadata metadata) {
        this.orgnummer = metadata.getOrgnummer();
        this.enhetstype = metadata.getEnhetstype();
    }

    static String formattedDate(LocalDate localDate) {
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

    String createEHN() {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(49, ' ');

        String dateNowFormatted = getDateNowFormatted();
        String undersakstype = isUpdate() ? "EN" : "NY";

        stringBuilder.replace(0, "ENH".length(), "ENH")
                .replace(4, 4 + orgnummer.length(), orgnummer)
                .replace(13, 13 + enhetstype.length(), enhetstype)
                .replace(17, 18, isUpdate() ? "E" : "N")
                .replace(18, 18 + undersakstype.length(), undersakstype)
                .replace(22, 22 + (dateNowFormatted + dateNowFormatted).length(), dateNowFormatted + dateNowFormatted + "J")
                .append("\n");

        return stringBuilder.toString();
    }

    public abstract boolean isUpdate();

    public abstract Flatfil toFlatfil();
}
