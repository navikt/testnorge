package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public class Telefon extends ToFlatfil {
    private final String tlf;

    public Telefon(Metadata metadata, no.nav.registre.testnorge.libs.avro.organisasjon.Telefon telefon) {
        super(metadata);
        this.tlf = telefon.getTlf();
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    private String toRecord() {
        return LineBuilder
                .newBuilder("TFON", 21)
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
