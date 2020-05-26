package no.nav.registre.ereg.domain;

import no.nav.registre.ereg.provider.rs.request.KnytningRs;

public class Knytning extends Flatfil {
    private final String record;

    public Knytning(String record) {
        this.record = record;
    }

    public KnytningRs toKnytningRs(String record) {
        return KnytningRs.builder()
                .type(getType())
                .ansvarsandel(getAnsvarsandel())
                .fratreden(getFratreden())
                .orgnr(getKnyttetOrgnr())
                .korrektOrgNr(getKorrektOrgNr())
                .valgtAv((getValgtAv()))
                .build();
    }

    public String getType() {
        return getValueFromRecord(record, 0, 8);
    }

    public String getAnsvarsandel() {
        return getValueFromRecord(record, 10, 40);
    }

    public String getFratreden() {
        return getValueFromRecord(record, 40, 41);
    }

    public String getKnyttetOrgnr() {
        return getValueFromRecord(record, 41, 50);
    }

    public String getValgtAv() {
        return getValueFromRecord(record, 50, 54);
    }

    public String getKorrektOrgNr() {
        return getValueFromRecord(record, 57, 66);
    }
}
