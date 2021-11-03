package no.nav.dolly.bestilling.organisasjonforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BestillingRequest {

    public enum MaalformType {B, N}

    public enum AdresseType {FADR, PADR}

    private List<SyntetiskOrganisasjon> organisasjoner;

    public List<SyntetiskOrganisasjon> getOrganisasjoner() {
        if (isNull(organisasjoner)) {
            organisasjoner = new ArrayList<>();
        }
        return organisasjoner;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SyntetiskOrganisasjon {

        @Schema(required = true, example = "BEDR", description = "I hht kodeverk EnhetstyperJuridiskEnhet eller EnhetstyperVirksomhet")
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

        private List<AdresseRequest> adresser;

        public List<AdresseRequest> getAdresser() {
            if (isNull(adresser)) {
                adresser = new ArrayList<>();
            }
            return adresser;
        }

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
        public static class AdresseRequest {
            private AdresseType adressetype;
            @Schema(maxLength = 35, description = "Inntil 3 adresselinjer a 35 tegn")
            private List<String> adresselinjer;
            @Schema(maxLength = 9, description = "Hvis landkode NO eller blank landkode: i hht kodeverk Postnummer, ellers fritt")
            private String postnr;
            @Schema(maxLength = 35, description = "Benyttes for utenlandsk poststed kun")
            private String poststed;
            @Schema(maxLength = 9, description = "Hvis landkode NO eller blank landkode: I hht kodeverk Kommuner")
            private String kommunenr;
            @Schema(maxLength = 3, description = "I hht kodeverk LandkoderISO2")
            private String landkode;
            @Schema(maxLength = 15, description = "Denne inneholder ID fra matrikkelen (når vi en gang får dette)")
            private String vegadresseId;

            public List<String> getAdresselinjer() {
                if (isNull(adresselinjer)) {
                    adresselinjer = new ArrayList<>();
                }
                return adresselinjer;
            }
        }
    }

}
