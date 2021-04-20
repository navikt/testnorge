package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktadresse extends PdlAdresse {

    private PdlVegadresse vegadresse;
    private PdlUtenlandskAdresse utenlandskAdresse;
    private PostadresseIFrittFormat postadresseIFrittFormat;
    private Postboksadresse postboksadresse;
    private UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat;
    private VegadresseForPost vegadresseForPost;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostadresseIFrittFormat implements Serializable {

        private List<String> adresselinjer;
        private String postnummer;

        public List<String> getAdresselinjer() {
            if (isNull(adresselinjer)) {
                adresselinjer = new ArrayList<>();
            }
            return adresselinjer;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Postboksadresse implements Serializable {
        private String postboks;
        private String postbokseier;
        private String postnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UtenlandskAdresseIFrittFormat implements Serializable {

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VegadresseForPost implements Serializable {

        private String adressekode;
        private String adressenavn;
        private String adressetillegsnavn;
        private String bruksenhetsnummer;
        private String husbokstav;
        private String husnummer;
        private String postnummer;
    }
}
