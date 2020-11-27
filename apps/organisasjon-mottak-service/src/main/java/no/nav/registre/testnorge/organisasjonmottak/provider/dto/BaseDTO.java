package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import org.apache.avro.specific.SpecificRecord;

public abstract class BaseDTO<T extends SpecificRecord> {
    private final String orgnummer;
    private final String enhetstype;

    public BaseDTO() {
        this.orgnummer = null;
        this.enhetstype = null;
    }

    public BaseDTO(String orgnummer, String enhetstype) {
        this.orgnummer = orgnummer;
        this.enhetstype = enhetstype;
    }

    public String getOrgnummer() {
        return orgnummer;
    }

    public String getEnhetstype() {
        return enhetstype;
    }

    public abstract T toRecord();
}
