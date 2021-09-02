package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPdlOrganisasjon extends RsPdlAdressat {

    public static final String ORGANISASJON = "ORGANISASJON";

    private PdlPersonnavn kontaktperson;
    private String organisasjonsnavn;
    private String organisasjonsnummer;

    @Override public String getAdressatType() {
        return ORGANISASJON;
    }
}
