package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsOrganisasjonBestilling {

    @Schema(description = "Liste av miljøer bestillingen skal deployes til")
    private Set<String> environments;
    private SyntetiskOrganisasjon organisasjon;
    @Schema(description = "Navn på malbestillling")
    private String malBestillingNavn;

    public Set<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new HashSet<>();
        }
        return environments;
    }

    public enum MaalformType {B, N}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SyntetiskOrganisasjon {

        @Schema( example = "BEDR", description = "I hht kodeverk EnhetstyperJuridiskEnhet eller EnhetstyperVirksomhet")
        private String enhetstype;
        @Schema(example = "28.930", description = "I hht kodeverk Næringskoder")
        private String naeringskode;
        @Schema(example = "6100", description = "I hht kodeverk Sektorkoder")
        private String sektorkode;
        @Schema(example = "Oppnå utjevning mellom kulturelle forskjeller", description = "Fritekstfelt opptil 70 tegn")
        private String formaal;
        private String telefon;
        private String epost;
        private String nettside;
        private MaalformType maalform;
        private LocalDate stiftelsesdato;

        private Adresse forretningsadresse;
        private Adresse postadresse;

        private List<SyntetiskOrganisasjon> underenheter;

        public List<SyntetiskOrganisasjon> getUnderenheter() {
            if (isNull(underenheter)) {
                underenheter = new ArrayList<>();
            }
            return underenheter;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Adresse {

            private List<String> adresselinjer;
            private String postnr;
            private String poststed;
            private String kommunenr;
            private String landkode;
        }
    }
}