package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class PdlAdresse {

    public enum Adressegradering {UGRADERT, KLIENTADRESSE, FORTROLIG, STRENGT_FORTROLIG}

    public enum Bruksenhetstype {BOLIG, ANNET_ENN_BOLIG, FRITIDSBOLIG, IKKE_GODKJENT_BOLIG, UNUMMERERT_BRUKSENHET}

    public enum Master {FREG, PDL}

    public enum OppholdAnnetSted {MILITAER, UTENRIKS, PAA_SVALBARD, PENDLER}

    private String adresseIdentifikatorFraMatrikkelen;
    private Adressegradering adressegradering;
    private String coAdressenavn;
    private String kilde;
    private Master master;
    private String naerAdresseIdentifikatorFraMatrikkelen;
    private Vegadresse vegadresse;

    @Getter
    @Setter
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
}
