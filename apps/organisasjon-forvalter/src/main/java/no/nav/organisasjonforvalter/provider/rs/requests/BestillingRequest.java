package no.nav.organisasjonforvalter.provider.rs.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestillingRequest {

    public enum MaalformType {B, N}
    public enum AdresseType {FORRETNING_ADR, POST_ADR}

    private List<OrganisasjonRequest> organisasjoner;

    public List<OrganisasjonRequest> getOrganisasjoner() {
        return isNull(organisasjoner) ? (organisasjoner = new ArrayList<>()) : organisasjoner;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganisasjonRequest {

        @Schema(required = true, example = "BEDR", description = "I hht kodeverk EnhetstyperJuridiskEnhet eller EnhetstyperVirksomhet")
        private String enhetstype;
        @Schema(required = true, example = "28.930", description = "I hht kodeverk Næringskoder")
        private String naeringskode;
        @Schema(required = true, example = "6100", description = "I hht kodeverk Sektorkoder")
        private String sektorkode;
        @Schema(required = true, example = "Oppnå utjevning mellom kulturelle forskjeller", description = "Fritekstfelt opptil 70 tegn")
        private String formaal;
        private String telefon;
        private String epost;
        private String nettside;
        private MaalformType maalform;
        private List<Adresse> adresser;
        private List<OrganisasjonRequest> underenheter;

        public List<Adresse> getAdresser() {
            return isNull(adresser) ? (adresser = new ArrayList<>()) : adresser;
        }

        public List<OrganisasjonRequest> getUnderenheter() {
            return isNull(underenheter) ? (underenheter = new ArrayList<>()) : underenheter;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adresse {

        private AdresseType adressetype;
        private List<String> adresselinjer;
        private String postnr;
        private String kommunenr;
        private String landkode;
        private String gatekode;
        private String boenhet;

        public List<String> getAdresselinjer() {
            return isNull(adresselinjer) ? (adresselinjer = new ArrayList<>()) : adresselinjer;
        }
    }
}
