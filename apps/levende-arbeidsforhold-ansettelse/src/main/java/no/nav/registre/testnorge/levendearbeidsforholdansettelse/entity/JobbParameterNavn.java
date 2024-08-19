package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

public enum JobbParameterNavn {
    ANTALL_ORGANISASJONER ("antallOrganisasjoner"),
    ANTALL_PERSONER ("antallPersoner"),
    INTERVALL ("intervall"),
    STILLINGSPROSENT ("stillingsprosent"),
    ARBEIDSFORHOLD_TYPE ("arbeidsforholdType");

    public final String value;
    JobbParameterNavn(String value){this.value = value;}
}
