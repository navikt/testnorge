package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public abstract class ToLine {

    private final String orgnummer;
    private final String enhetstype;
    private final String miljo;
    private final String uuid;

    public ToLine(Metadata metadata, String uuid) {
        this.orgnummer = metadata.getOrgnummer();
        this.enhetstype = metadata.getEnhetstype();
        this.miljo = metadata.getMiljo();
        this.uuid = uuid;
    }

    String getDateFormatted(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    abstract ValueBuilder builder();

    public String getEnhetstype() {
        return enhetstype;
    }

    public String getOrgnummer() {
        return orgnummer;
    }

    public final Line toLine() {
        return Line
                .builder()
                .enhetstype(enhetstype)
                .orgnummer(orgnummer)
                .value(builder().toString())
                .updatable(isUpdatable())
                .uuid(uuid)
                .miljo(miljo)
                .build();
    }

    public boolean isUpdatable() {
        return true;
    }
}
