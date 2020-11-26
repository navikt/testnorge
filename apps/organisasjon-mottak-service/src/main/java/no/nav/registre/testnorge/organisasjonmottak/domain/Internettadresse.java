package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Internettadresse extends ToFlatfil {
    String internettadresse;

    private String toInternettadresse() {
        return LineBuilder
                .newBuilder("IADR", 158)
                .setLine(8, internettadresse)
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
        record.append(toInternettadresse());
        flatfil.add(record);
        return flatfil;
    }
}
