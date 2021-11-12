package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KontaktinformasjonForDoedsboDTO extends DbVersjonDTO {

    @Schema(required = true,
            description = "Dødsboets skifteform")
    private PdlSkifteform skifteform;

    @Schema(required = true,
            type = "LocalDateTime",
            description = "Dato for utstedelse")
    private LocalDateTime attestutstedelsesdato;

    private KontaktinformasjonForDoedsboAdresse adresse;

    private OrganisasjonDTO advokatSomKontakt;
    private KontaktpersonDTO personSomKontakt;
    private OrganisasjonDTO organisasjonSomKontakt;

    public enum PdlSkifteform {

        OFFENTLIG, ANNET
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class KontaktinformasjonForDoedsboAdresse implements Serializable {

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
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class KontaktpersonDTO implements Serializable {

        private String identifikasjonsnummer;
        private LocalDateTime foedselsdato;
        private PersonNavnDTO navn;

        private Boolean isIdentExternal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class PersonNavnDTO implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
        private Boolean hasMellomnavn;
    }


    public int countKontakter() {

        return count(getAdvokatSomKontakt()) + count(getPersonSomKontakt()) + count(getOrganisasjonSomKontakt());
    }
}