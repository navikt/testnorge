package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public class Mobiltelefon extends ToLine {
    private final String tlf;

    public Mobiltelefon(String uuid, Metadata metadata, no.nav.registre.testnorge.libs.avro.organisasjon.Mobiltelefon mobiltelefon) {
        super(metadata, uuid);
        this.tlf = mobiltelefon.getTlf();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("MTLF", 21)
                .setLine(8, tlf);
    }

}
