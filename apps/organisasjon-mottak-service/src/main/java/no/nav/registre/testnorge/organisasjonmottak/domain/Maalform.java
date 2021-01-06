package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public class Maalform extends ToLine {
    private final String maalform;

    public Maalform(String uuid, Metadata metadata, no.nav.registre.testnorge.libs.avro.organisasjon.Maalform maalform) {
        super(metadata, uuid);
        this.maalform = maalform.getMaalform();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("MÅL", 9)
                .setLine(8, maalform);
    }

}
