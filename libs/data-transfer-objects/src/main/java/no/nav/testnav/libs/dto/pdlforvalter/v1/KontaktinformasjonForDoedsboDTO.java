package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KontaktinformasjonForDoedsboDTO extends DbVersjonDTO {

    @Schema(description = "Dødsboets adresse, adresselinje 1")
    private String adresselinje1;

    @Schema(description = "Dødsboets adresse, adresselinje 2")
    private String adresselinje2;

    @Schema(description = "Postnummer i hht kodeverk 'Postnummer'")
    private String postnummer;

    @Schema(description = "Poststed i hht kodeverk 'Postnummer'")
    private String poststedsnavn;

    @Schema(description = "Landkode i hht. kodeverk 'Landkoder'")
    private String landkode;

    @Schema(required = true,
            description = "Dødsboets skifteform")
    private PdlSkifteform skifteform;

    @Schema(required = true,
            type = "LocalDateTime",
            description = "Dato for utstedelse")
    private LocalDateTime utstedtDato;

    private AdressatDTO adressat;

    public enum PdlSkifteform {

        OFFENTLIG, ANNET
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AdressatDTO implements Serializable {

        private OrganisasjonDTO advokatSomAdressat;
        private KontaktpersonMedIdNummerDTO kontaktpersonMedIdNummerSomAdressat;
        private KontaktpersonUtenIdNummerDTO kontaktpersonUtenIdNummerSomAdressat;
        private OrganisasjonDTO organisasjonSomAdressat;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class KontaktpersonMedIdNummerDTO implements Serializable {

        private String idnummer;
        private PersonRequestDTO nyKontaktPerson;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class KontaktpersonUtenIdNummerDTO implements Serializable {

        private LocalDateTime foedselsdato;
        private PersonNavnDTO navn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrganisasjonDTO implements Serializable {

        private PersonNavnDTO kontaktperson;
        private String organisasjonsnavn;
        @Schema(required = true)
        private String organisasjonsnummer;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PersonNavnDTO implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
        private Boolean hasMellomnavn;
    }
}