package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Forretningsadresse extends Adresse {

    public Forretningsadresse(no.nav.registre.testnorge.libs.avro.organiasjon.Forretningsadresse forretningsadresse) {
        super(
                forretningsadresse.getMetadata(),
                forretningsadresse.getPostnummer(),
                forretningsadresse.getLandkode(),
                forretningsadresse.getKommunenummer(),
                forretningsadresse.getPoststed(),
                forretningsadresse.getPostadresse1(),
                forretningsadresse.getPostadresse2(),
                forretningsadresse.getPostadresse3(),
                forretningsadresse.getLinjenummer(),
                forretningsadresse.getVegadresseId()
        );
    }

    @Override
    public String getFelttype() {
        return "FADR";
    }
}