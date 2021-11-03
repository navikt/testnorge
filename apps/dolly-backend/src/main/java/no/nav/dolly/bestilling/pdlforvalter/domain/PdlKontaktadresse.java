package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktadresse extends PdlAdresse {

    private PostadresseIFrittFormat postadresseIFrittFormat;
    private Postboksadresse postboksadresse;
    private UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat;
    private VegadresseForPost vegadresseForPost;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostadresseIFrittFormat {

        private List<String> adresselinjer;
        private String postnummer;

        public List<String> getAdresselinjer() {
            if (isNull(adresselinjer)) {
                adresselinjer = new ArrayList<>();
            }
            return adresselinjer;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Postboksadresse {
        private String postboks;
        private String postbokseier;
        private String postnummer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtenlandskAdresse {
        private String adressenavnNummer;
        private String boenhet;
        private String bySted;
        private String bygning;
        private String bygningEtasjeLeilighet;
        private String distriktsnavn;
        private String etasjenummer;
        private String landkode;
        private String postboksNummerNavn;
        private String postkode;
        private String region;
        private String regionDistriktOmraade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UtenlandskAdresseIFrittFormat {

        private List<String> adresselinjer;
        private String byEllerStedsnavn;
        private String landkode;
        private String postkode;

        public List<String> getAdresselinjer() {
            if (isNull(adresselinjer)) {
                adresselinjer = new ArrayList<>();
            }
            return adresselinjer;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VegadresseForPost {

        private String adressekode;
        private String adressenavn;
        private String adressetillegsnavn;
        private String bruksenhetsnummer;
        private String husbokstav;
        private String husnummer;
        private String postnummer;
    }
}
