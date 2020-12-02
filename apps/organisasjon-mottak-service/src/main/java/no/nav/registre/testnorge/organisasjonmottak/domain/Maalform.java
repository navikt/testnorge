package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;

public class Maalform extends ToFlatfil {
    private final String maalform;

    public Maalform(Metadata metadata, no.nav.registre.testnorge.libs.avro.organiasjon.Maalform maalform) {
        super(metadata);
        this.maalform = maalform.getMaalform();
    }

    private String toRecordLine() {
        return LineBuilder
                .newBuilder("MÅL", 9)
                .setLine(8, maalform)
                .toString();
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    @Override
    public Flatfil toFlatfil() {
        Flatfil flatfil = new Flatfil();
        Record record = new Record();
        record.append(createEHN());
        record.append(toRecordLine());
        flatfil.add(record);
        return flatfil;
    }
}
