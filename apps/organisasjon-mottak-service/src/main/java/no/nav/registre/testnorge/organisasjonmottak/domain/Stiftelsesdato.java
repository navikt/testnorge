package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;

public class Stiftelsesdato extends ToFlatfil {
    private final LocalDate localDate;

    public Stiftelsesdato(Metadata metadata, no.nav.registre.testnorge.libs.avro.organiasjon.Stiftelsesdato stiftelsesdato) {
        super(metadata);
        localDate = LocalDate.of(
                stiftelsesdato.getAar(),
                stiftelsesdato.getMaaned(),
                stiftelsesdato.getDag()
        );
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    private String getDateFormatted() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(localDate);
    }

    private String toRecord() {
        return LineBuilder
                .newBuilder("STID", 16)
                .setLine(8, getDateFormatted())
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
