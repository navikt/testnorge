package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import lombok.Getter;

public enum JobbParameterNavn {
    ANTALL_ORGANISASJONER ("antallOrganisasjoner"),
    ANTALL_PERSONER ("antallPersoner"),
    INTERVALL ("intervall");

    public final String value;
    JobbParameterNavn(String value){this.value = value;}
}
