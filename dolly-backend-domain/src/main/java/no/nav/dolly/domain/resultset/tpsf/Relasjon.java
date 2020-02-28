package no.nav.dolly.domain.resultset.tpsf;

import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.BARN;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.EKTEFELLE;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.FAR;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.FOEDSEL;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.MOR;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.PARTNER;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relasjon {

    public enum ROLLE {PARTNER, EKTEFELLE, MOR, FAR, FOEDSEL, BARN}

    private Long id;
    private Person person;
    private Person personRelasjonMed;
    private ROLLE relasjonTypeNavn;

    public boolean isPartner() {
        return PARTNER == getRelasjonTypeNavn() || EKTEFELLE == getRelasjonTypeNavn();
    }

    public boolean isBarn() {
        return FOEDSEL == getRelasjonTypeNavn() || BARN == getRelasjonTypeNavn();
    }

    public boolean isMor() {
        return MOR == getRelasjonTypeNavn();
    }

    public boolean isFar() {
        return FAR == getRelasjonTypeNavn();
    }
}