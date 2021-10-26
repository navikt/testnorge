package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktinformasjonForDoedsbo extends PdlOpplysning {

    private KontaktinformasjonForDoedsboAdresse adresse;
    private LocalDate attestutstedelsesdato;

    private OrganisasjonSomKontakt advokatSomKontakt;
    private PersonSomKontakt personSomKontakt;
    private OrganisasjonSomKontakt organisasjonSomKontakt;

    private PdlSkifteform skifteform;

    public enum PdlSkifteform {

        OFFENTLIG, ANNET
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class KontaktinformasjonForDoedsboAdresse {

        private String adresselinje1;
        private String adresselinje2;
        private String postnummer;
        private String poststedsnavn;
        private String landkode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PersonSomKontakt {

        private LocalDate foedselsdato;
        private String identifikasjonsnummer;
        private Personnavn navn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrganisasjonSomKontakt {

        private Personnavn kontaktperson;
        private String organisasjonsnavn;
        private String organisasjonsnummer;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Personnavn {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}