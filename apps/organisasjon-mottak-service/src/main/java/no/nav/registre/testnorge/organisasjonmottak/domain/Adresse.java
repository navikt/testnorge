package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;

@Data
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public abstract class Adresse extends ToFlatfil {
    private final String postnummer;
    private final String landkode;
    private final String kommunenummer;
    private final String poststed;
    private final String postadresse1;
    private final String postadresse2;
    private final String postadresse3;
    private final String linjenummer;
    private final String vegadresseId;

    public Adresse(Metadata metadata, String postnummer, String landkode, String kommunenummer, String poststed, String postadresse1, String postadresse2, String postadresse3, String linjenummer, String vegadresseId) {
        super(metadata);
        this.postnummer = postnummer;
        this.landkode = landkode;
        this.kommunenummer = kommunenummer;
        this.poststed = poststed;
        this.postadresse1 = postadresse1;
        this.postadresse2 = postadresse2;
        this.postadresse3 = postadresse3;
        this.linjenummer = linjenummer;
        this.vegadresseId = vegadresseId;
    }

    public abstract String getFelttype();


    private String createAdresse() {
        StringBuilder builder = createBaseStringbuilder(185, getFelttype(), "N");
        setLine(builder, 8, postnummer);
        setLine(builder, 17, landkode);
        setLine(builder, 20, kommunenummer);
        setLine(builder, 29, poststed);
        setLine(builder, 64, postadresse1);
        setLine(builder, 99, postadresse2);
        setLine(builder, 134, postadresse3);
        setLine(builder, 169, linjenummer);
        setLine(builder, 170, vegadresseId);
        return builder.toString();
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
        record.append(createAdresse());
        flatfil.add(record);
        return flatfil;
    }
}
