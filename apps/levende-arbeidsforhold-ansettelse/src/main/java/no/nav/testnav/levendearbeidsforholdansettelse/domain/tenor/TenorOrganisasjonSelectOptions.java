package no.nav.testnav.levendearbeidsforholdansettelse.domain.tenor;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TenorOrganisasjonSelectOptions {

    @Getter
    @AllArgsConstructor
    public enum OrganisasjonForm  {
        BEDR("Underenhet til næringsdrivende og offentlig forvaltning");


        private final String label;
    }
}