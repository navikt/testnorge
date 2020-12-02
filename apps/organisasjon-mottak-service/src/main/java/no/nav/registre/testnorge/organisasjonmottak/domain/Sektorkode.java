package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;

public class Sektorkode extends ToFlatfil {
    private final String kode;

    public Sektorkode(Metadata metadata, no.nav.registre.testnorge.libs.avro.organiasjon.Sektorkode sektorkode) {
        super(metadata);
        this.kode = sektorkode.getSektorkode();
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    private String toRecord() {
        return LineBuilder
                .newBuilder("ISEK", 12)
                .setLine(8, kode)
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
