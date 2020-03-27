package no.nav.registre.sdForvalter.domain.status.ereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import no.nav.registre.sdForvalter.domain.Ereg;

@Value
public class OrganisasjonStatus {
    @JsonProperty
    private Organisasjon fasteData;
    @JsonProperty
    private Organisasjon fraEreg;

    public OrganisasjonStatus(Ereg fasteData, Organisasjon fraEreg) {
        this.fasteData = new Organisasjon(fasteData);
        this.fraEreg = fraEreg;
    }
}
