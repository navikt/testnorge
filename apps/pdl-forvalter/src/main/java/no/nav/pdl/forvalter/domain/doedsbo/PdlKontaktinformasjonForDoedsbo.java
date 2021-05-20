package no.nav.pdl.forvalter.domain.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktinformasjonForDoedsbo extends PdlDbVersjon {

    private Adressat adressat;
    private String adresselinje1;
    private String adresselinje2;
    private String landkode;
    private String postnummer;
    private String poststedsnavn;
    private PdlSkifteform skifteform;
    private LocalDateTime utstedtDato;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Adressat implements Serializable {

        private PdlAdvokat advokatSomAdressat;
        private PdlKontaktpersonMedIdNummer kontaktpersonMedIdNummerSomAdressat;
        private PdlKontaktpersonUtenIdNummer kontaktpersonUtenIdNummerSomAdressat;
        private PdlOrganisasjon organisasjonSomAdressat;
    }
}
