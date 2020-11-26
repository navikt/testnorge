package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Postadresse extends Adresse {

    public Postadresse(no.nav.registre.testnorge.libs.avro.organiasjon.Posstadresse posstadresse) {
        super(
                posstadresse.getMetadata(),
                posstadresse.getPostnummer(),
                posstadresse.getLandkode(),
                posstadresse.getKommunenummer(),
                posstadresse.getPoststed(),
                posstadresse.getPostadresse1(),
                posstadresse.getPostadresse2(),
                posstadresse.getPostadresse3(),
                posstadresse.getLinjenummer(),
                posstadresse.getVegadresseId()
        );
    }

    @Override
    public String getFelttype() {
        return "PADR";
    }
}