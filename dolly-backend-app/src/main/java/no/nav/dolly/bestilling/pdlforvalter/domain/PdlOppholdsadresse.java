package no.nav.dolly.bestilling.pdlforvalter.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlOppholdsadresse {

    public enum Adressegradering {UGRADERT, KLIENTADRESSE, FORTROLIG, STRENGT_FORTROLIG}

    public enum Master {FREG, PDL}

    public enum OppholdAnnetSted {MILITAER, UTENRIKS, PAA_SVALBARD, PENDLER}

    public enum Bruksenhetstype {BOLIG, ANNET_ENN_BOLIG, FRITIDSBOLIG, IKKE_GODKJENT_BOLIG, UNUMMERERT_BRUKSENHET}

    private String adresseIdentifikatorFraMatrikkelen;
    private Adressegradering adressegradering;
    private String coAdressenavn;
    private String kilde;
    private Master master;
    private String naerAdresseIdentifikatorFraMatrikkelen;
    private OppholdAnnetSted oppholdAnnetSted;
    private LocalDate oppholdsadressedato;
    private Vegadresse vegadresse;
    private UtenlandskAdresse utenlandskAdresse;
    private Matrikkeladresse matrikkeladresse;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vegadresse {

        private String adressekode;
        private String adressenavn;
        private String adressetillegsnavn;
        private String bruksenhetsnummer;
        private Bruksenhetstype bruksenhetstype;
        private String husbokstav;
        private String husnummer;
        private String kommunenummer;
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
    public static class Matrikkeladresse {

        private String adressetilleggsnavn;
        private String bruksenhetsnummer;
        private Bruksenhetstype bruksenhetstype;
        private Integer bruksnummer;
        private Integer festenummer;
        private Integer gaardsnummer;
        private String kommunenummer;
        private String postnummer;
        private Integer undernummer;
    }
}
