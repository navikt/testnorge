package no.nav.dolly.bestilling.aareg.amelding.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.ameldingservice.v1.PersonDTO;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Virksomhet {

    private String organisajonsnummer;
    private List<PersonDTO> personer;

    public String getOrganisajonsnummer() {
        return this.organisajonsnummer;
    }

    public List<PersonDTO> getPersoner() {
        return this.personer;
    }

}
