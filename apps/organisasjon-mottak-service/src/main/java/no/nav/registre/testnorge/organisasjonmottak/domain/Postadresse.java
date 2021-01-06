package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Postadresse extends Adresse {

    public Postadresse(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.Postadresse postadresse) {
        super(
                postadresse.getMetadata(),
                postadresse.getPostnummer(),
                postadresse.getLandkode(),
                postadresse.getKommunenummer(),
                postadresse.getPoststed(),
                postadresse.getPostadresse1(),
                postadresse.getPostadresse2(),
                postadresse.getPostadresse3(),
                postadresse.getLinjenummer(),
                postadresse.getVegadresseId(),
                uuid
        );
    }

    @Override
    public String getFelttype() {
        return "PADR";
    }

}