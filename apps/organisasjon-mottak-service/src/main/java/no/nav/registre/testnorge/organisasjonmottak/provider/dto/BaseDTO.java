package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import org.apache.avro.specific.SpecificRecord;

import java.time.LocalDate;

import no.nav.registre.testnorge.libs.avro.organisasjon.Dato;

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


    Dato toDato(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Dato.newBuilder()
                .setAar(date.getYear())
                .setDag(date.getDayOfMonth())
                .setMaaned(date.getMonth().getValue())
                .build();
    }

    public String getOrgnummer() {
        return orgnummer;
    }

    public String getEnhetstype() {
        return enhetstype;
    }

    public abstract T toRecord(String miljoe);
}
