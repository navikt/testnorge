package no.nav.testnav.levendearbeidsforholdansettelse.domain.kodeverk;

public enum KodeverkNavn {
    YRKER ("Yrker"),
    ARBEIDSTIDSORDNINGER ("Arbeidstidsordninger"),
    AVLOENNINGSTYPER ("Avlønningstyper"),
    PERMISJONOGPERMITTERINGSBESKRIVELSE ("PermisjonsOgPermitteringsBeskrivelse"),
    SLUUAARSAKAAREG ("SluttårsakAareg"),
    ANSETTELSESFORMAAREG ("AnsettelsesformAareg"),
    SKIPSREGISTRE ("Skipsregistre"),
    SKIPSTYPER ("Skipstyper"),
    FARTSOMRAADER ("Fartsområder"),
    VALUTAER ("Valutaer");

    public final String value;

    KodeverkNavn(String value) {
        this.value = value;
    }
    String getValue() {
        return value;
    }
}
