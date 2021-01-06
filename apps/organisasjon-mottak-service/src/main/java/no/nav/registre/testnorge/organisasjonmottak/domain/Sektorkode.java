package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public class Sektorkode extends ToLine {
    private final String kode;

    public Sektorkode(String uuid, Metadata metadata, no.nav.registre.testnorge.libs.avro.organisasjon.Sektorkode sektorkode) {
        super(metadata, uuid);
        this.kode = sektorkode.getSektorkode();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("ISEK", 12)
                .setLine(8, kode);
    }
}
