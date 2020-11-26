package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Organiasjon extends ToFlatfil {
    String navn;

    public Organiasjon(no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon organiasjon) {
        super(organiasjon.getOrgnummer(), organiasjon.getEnhetstype());
        this.navn = organiasjon.getNavn();
    }

    private String createNavn() {
        StringBuilder stringBuilder = createBaseStringbuilder(219, "NAVN", "N");
        stringBuilder.replace(8, 8 + navn.length(), navn).append("\n");
        stringBuilder.replace(183, 183 + navn.length(), navn).append("\n");
        return stringBuilder.toString();
    }

    @Override
    public boolean isUpdate() {
        return false;
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