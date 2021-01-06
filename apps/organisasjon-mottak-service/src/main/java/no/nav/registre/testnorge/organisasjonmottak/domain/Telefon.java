package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public class Telefon extends ToLine {
    private final String tlf;

    public Telefon(String uuid, Metadata metadata, no.nav.registre.testnorge.libs.avro.organisasjon.Telefon telefon) {
        super(metadata, uuid);
        this.tlf = telefon.getTlf();
    }


    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("TFON", 21)
                .setLine(8, tlf);
    }
}
