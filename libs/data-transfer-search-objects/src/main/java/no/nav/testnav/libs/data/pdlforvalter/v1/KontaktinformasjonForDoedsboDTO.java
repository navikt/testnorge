package no.nav.testnav.libs.data.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KontaktinformasjonForDoedsboDTO extends DbVersjonDTO {

    @Schema(
            description = "Dødsboets skifteform")
    private PdlSkifteform skifteform;

    @Schema(
            type = "LocalDateTime",
            description = "Dato for utstedelse")
    private LocalDateTime attestutstedelsesdato;

    private KontaktinformasjonForDoedsboAdresse adresse;

    private OrganisasjonDTO advokatSomKontakt;
    private KontaktpersonDTO personSomKontakt;
    private OrganisasjonDTO organisasjonSomKontakt;

    public int countKontakter() {

        return count(getAdvokatSomKontakt()) + count(getPersonSomKontakt()) + count(getOrganisasjonSomKontakt());
    }

    public enum PdlSkifteform {

        OFFENTLIG, ANNET
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
    public static class KontaktpersonDTO implements Serializable {

        private String identifikasjonsnummer;
        private PersonRequestDTO nyKontaktperson;

        private LocalDateTime foedselsdato;
        private PersonNavnDTO navn;

        private Boolean eksisterendePerson;

        public boolean isEksisterendePerson() {

            return isTrue(eksisterendePerson);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganisasjonDTO implements Serializable {

        private PersonNavnDTO kontaktperson;
        private String organisasjonsnavn;
        @Schema
        private String organisasjonsnummer;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonNavnDTO implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
        private Boolean hasMellomnavn;
    }

    @JsonIgnore
    @Override
    public String getIdentForRelasjon() {
        return nonNull(getPersonSomKontakt()) ?
                personSomKontakt.getIdentifikasjonsnummer() : null;
    }
}