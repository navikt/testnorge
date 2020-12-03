package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.time.LocalDate;

public class Nearingskode extends ToFlatfil {
    private final String kode;
    private final LocalDate gyldighetsdato;
    private final boolean hjelpeenhet;

    public Nearingskode(no.nav.registre.testnorge.libs.avro.organisasjon.Nearingskode nearingskode) {
        super(nearingskode.getMetadata());
        this.kode = nearingskode.getKode();
        this.gyldighetsdato = LocalDate.of(
                nearingskode.getGyldighetsdato().getAar(),
                nearingskode.getGyldighetsdato().getMaaned(),
                nearingskode.getGyldighetsdato().getDag()
        );
        this.hjelpeenhet = nearingskode.getHjelpeenhet();
    }

    private String toRecordLine() {
        return LineBuilder
                .newBuilder("NACE", 23)
                .setLine(8, kode)
                .setLine(14, formattedDate(gyldighetsdato))
                .setLine(22, hjelpeenhet ? "J" : "N")
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
