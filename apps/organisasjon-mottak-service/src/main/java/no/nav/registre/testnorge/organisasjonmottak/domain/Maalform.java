package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Maalform extends ToFlatfil {
    String maalform;

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
