package no.nav.organisasjonforvalter.provider.rs.requests;

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

    public enum AdresseType {FORRETNING_ADR, POST_ADR}

    private List<OrganisasjonRequest> organisasjoner;

    public List<OrganisasjonRequest> getOrganisasjoner() {
        return isNull(organisasjoner) ? (organisasjoner = new ArrayList<>()) : organisasjoner;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganisasjonRequest {

        private String enhetstype;
        private String organisasjonsform;
        private String naeringskode;
        private String formaal;
        private String telefon;
        private String epost;
        private String nettside;
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
