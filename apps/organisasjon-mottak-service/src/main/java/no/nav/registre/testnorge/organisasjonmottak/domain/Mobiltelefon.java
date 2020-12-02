package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;

public class Mobiltelefon extends ToFlatfil {
    private final String tlf;

    public Mobiltelefon(Metadata metadata, no.nav.registre.testnorge.libs.avro.organiasjon.Mobiltelefon mobiltelefon) {
        super(metadata);
        this.tlf = mobiltelefon.getTlf();
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    private String toRecord() {
        return LineBuilder
                .newBuilder("MTLF", 21)
                .setLine(8, tlf)
                .toString();
    }

    @Override
    public Flatfil toFlatfil() {
        Flatfil flatfil = new Flatfil();
        Record record = new Record();
        record.append(createEHN());
        record.append(toRecord());
        flatfil.add(record);
        return flatfil;
    }
}
