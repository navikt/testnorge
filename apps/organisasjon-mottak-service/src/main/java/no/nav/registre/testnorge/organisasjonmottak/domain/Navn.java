package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Navn extends ToFlatfil {
    String navn;

    public Navn(no.nav.registre.testnorge.libs.avro.organiasjon.Navn navn) {
        super(navn.getMetadata());
        this.navn = navn.getNavn();
    }

    private String createNavn() {
        StringBuilder stringBuilder = createBaseStringbuilder(219, "NAVN", "N");
        stringBuilder.replace(8, 8 + navn.length(), navn).append("\n");
        stringBuilder.replace(183, 183 + navn.length(), navn).append("\n");
        return stringBuilder.toString();
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
        record.append(createNavn());
        flatfil.add(record);
        return flatfil;
    }
}
