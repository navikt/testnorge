package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.pdl.forvalter.dto.RsNavn;
import no.nav.pdl.forvalter.dto.RsPersonRequest;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktinformasjonForDoedsbo extends PdlDbVersjon {

    @Schema(required = true,
            description = "Dødsboets adresse, adresselinje 1")
    private String adresselinje1;

    @Schema(description = "Dødsboets adresse, adresselinje 2")
    private String adresselinje2;

    @Schema(required = true,
            description = "Postnummer i hht kodeverk 'Postnummer'")
    private String postnummer;

    @Schema(required = true,
            description = "Poststed i hht kodeverk 'Postnummer'")
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

    private Adressat adressat;

    @Data
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PdlAdvokat implements Serializable {

        private RsNavn kontaktperson;
        private String organisasjonsnavn;
        private String organisasjonsnummer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PdlKontaktpersonMedIdNummer implements Serializable{

        private String idnummer;
        private RsPersonRequest nyKontaktPerson;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PdlKontaktpersonUtenIdNummer implements Serializable {

        private LocalDateTime foedselsdato;
        private RsNavn navn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PdlOrganisasjon implements Serializable {

        private RsNavn kontaktperson;
        private String organisasjonsnavn;
        private String organisasjonsnummer;
    }

    public enum PdlSkifteform {

        OFFENTLIG, ANNET
    }
}