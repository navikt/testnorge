package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

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
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(localDate);
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
