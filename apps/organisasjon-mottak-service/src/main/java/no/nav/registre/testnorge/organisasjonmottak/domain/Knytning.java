package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public class Knytning extends ToFlatfil {
    private final String overenhetOrgnummer;
    private final String overenhetEnhetstype;

    public Knytning(Metadata metadata, String overenhetOrgnummer, String overenhetEnhetstype) {
        super(metadata);
        this.overenhetOrgnummer = overenhetOrgnummer;
        this.overenhetEnhetstype = overenhetEnhetstype;
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    private String toRecordLine() {
        return LineBuilder
                .newBuilder(overenhetEnhetstype, 66)
                .setLine(5, "SSY")
                .setLine(8, "K")
                .setLine(9, "D")
                .setLine(41, overenhetOrgnummer)
                .toString();
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
