package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import no.nav.registre.testnorge.organisasjonmottak.domain.ToFlatfil;

public abstract class BaseDTO<T extends ToFlatfil> {
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

    public abstract T toDomain();
}
