package no.nav.dolly.bestilling.pdlforvalter.domain;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlKontaktadresse {

    public enum Adressegradering {UGRADERT, KLIENTADRESSE, FORTROLIG, STRENGT_FORTROLIG}

    public enum Master {FREG, PDL}

    public enum Bruksenhetstype {BOLIG, ANNET_ENN_BOLIG, FRITIDSBOLIG, IKKE_GODKJENT_BOLIG, UNUMMERERT_BRUKSENHET}

    private String adresseIdentifikatorFraMatrikkelen;
    private Adressegradering adressegradering;
    private String coAdressenavn;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;
    private String kilde;
    private Master master;
    private String naerAdresseIdentifikatorFraMatrikkelen;
    private PostadresseIFrittFormat postadresseIFrittFormat;
    private Postboksadresse postboksadresse;
    private UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat;
    private VegadresseForPost vegadresseForPost;
    private Vegadresse vegadresse;

    @Getter
    @Setter
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Postboksadresse {
        private String postboks;
        private String postbokseier;
        private String postnummer;
    }

    @Getter
    @Setter
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VegadresseForPost {

        private String adressekode;
        private String adressenavn;
        private String adressetillegsnavn;
        private String bruksenhetsnummer;
        private String husbokstav;
        private String husnummer;
        private String postnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vegadresse {

        private String adressekode;
        private String adressenavn;
        private String adressetilleggsnavn;
        private String bruksenhetsnummer;
        private Bruksenhetstype bruksenhetstype;
        private String husbokstav;
        private String husnummer;
        private String kommunenummer;
        private String postnummer;
    }
}
