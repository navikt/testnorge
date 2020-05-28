package no.nav.registre.sdForvalter.domain.status.ereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import no.nav.registre.sdForvalter.domain.Ereg;

@Value
public class OrganisasjonStatus {
    @JsonProperty
    private String orgnummer;
    @JsonProperty
    private Organisasjon fasteData;
    @JsonProperty
    private Organisasjon fraEreg;

    public OrganisasjonStatus(String orgnummer, Ereg fasteData, Organisasjon fraEreg) {
        this.orgnummer = orgnummer;
        this.fasteData = new Organisasjon(fasteData);
        this.fraEreg = fraEreg;
    }

    @JsonProperty
    public boolean isEqual() {
        if (fraEreg == null) {
            return false;
        }
        return fasteData.equals(fraEreg);
    }
}
