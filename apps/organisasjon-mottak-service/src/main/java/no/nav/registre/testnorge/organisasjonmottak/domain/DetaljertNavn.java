package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class DetaljertNavn extends ToFlatfil {
    String navn1;
    String navn2;
    String navn3;
    String navn4;
    String navn5;
    String redigertNavn;

    public DetaljertNavn(no.nav.registre.testnorge.libs.avro.organiasjon.DetaljertNavn detaljertNavn) {
        super(detaljertNavn.getMetadata());
        this.navn1 = detaljertNavn.getNavn1();
        this.navn2 = detaljertNavn.getNavn2();
        this.navn3 = detaljertNavn.getNavn3();
        this.navn4 = detaljertNavn.getNavn4();
        this.navn5 = detaljertNavn.getNavn5();
        this.redigertNavn = detaljertNavn.getRedigertNavn();
    }

    private String createNavn() {
        StringBuilder stringBuilder = createBaseStringbuilder(219, "NAVN", "N");
        stringBuilder.replace(8, 8 + navn1.length(), navn1).append("\n");

        if (navn2 != null) {
            stringBuilder.replace(43, 43 + navn2.length(), navn2).append("\n");
        }
        if (navn3 != null) {
            stringBuilder.replace(78, 78 + navn3.length(), navn3).append("\n");
        }
        if (navn4 != null) {
            stringBuilder.replace(113, 113 + navn4.length(), navn4).append("\n");
        }
        if (navn5 != null) {
            stringBuilder.replace(148, 148 + navn5.length(), navn5).append("\n");
        }
        if (redigertNavn != null) {
            stringBuilder.replace(183, 183 + redigertNavn.length(), redigertNavn).append("\n");
        }
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
