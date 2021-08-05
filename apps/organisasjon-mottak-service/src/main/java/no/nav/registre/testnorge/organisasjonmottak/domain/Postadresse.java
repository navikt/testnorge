package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Postadresse extends Adresse {

    public Postadresse(no.nav.testnav.libs.avro.organisasjon.v1.Adresse postadresse) {
        super(
                postadresse.getPostnummer(),
                postadresse.getLandkode(),
                postadresse.getKommunenummer(),
                postadresse.getPoststed(),
                postadresse.getPostadresse1(),
                postadresse.getPostadresse2(),
                postadresse.getPostadresse3(),
                postadresse.getLinjenummer(),
                postadresse.getVegadresseId()
        );
    }

    @Override
    public String getFelttype() {
        return "PADR";
    }

}