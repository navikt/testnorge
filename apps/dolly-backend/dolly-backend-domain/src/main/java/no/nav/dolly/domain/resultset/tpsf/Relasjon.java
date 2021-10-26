package no.nav.dolly.domain.resultset.tpsf;

import lombok.Getter;
import lombok.Setter;

import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.BARN;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.EKTEFELLE;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.FAR;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.FOEDSEL;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.MOR;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.PARTNER;

@Getter
@Setter
public class Relasjon {

    public enum ROLLE {PARTNER, EKTEFELLE, MOR, FAR, FOEDSEL, BARN}

    private Long id;
    private Person person;
    private Person personRelasjonMed;
    private ROLLE relasjonTypeNavn;
    private Person personRelasjonTil;
    private Boolean fellesAnsvar;

    public boolean isPartner() {
        return PARTNER == getRelasjonTypeNavn() || EKTEFELLE == getRelasjonTypeNavn();
    }

    public boolean isBarn() {
        return FOEDSEL == getRelasjonTypeNavn() || BARN == getRelasjonTypeNavn();
    }

    public boolean isForelder() {
        return MOR == getRelasjonTypeNavn() || FAR == getRelasjonTypeNavn();
    }
}