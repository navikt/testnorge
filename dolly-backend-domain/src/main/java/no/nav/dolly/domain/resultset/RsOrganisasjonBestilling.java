package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsOrganisasjonBestilling {

    @Schema(description = "Liste av miljøer bestillingen skal deployes til")
    private List<String> environments;

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList<>();
        }
        return environments;
    }

    private SyntetiskOrganisasjon organisasjon;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SyntetiskOrganisasjon {

        private String enhetstype;
        private String naeringskode;
        private String formaal;
        private String telefon;
        private String epost;
        private String nettside;
        private LocalDateTime stiftelsesdato;

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
            private String kommunenr;
            private String landkode;
        }
    }
}