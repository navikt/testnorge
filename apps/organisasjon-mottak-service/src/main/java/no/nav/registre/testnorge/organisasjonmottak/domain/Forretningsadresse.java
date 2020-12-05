package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Forretningsadresse extends Adresse {

    public Forretningsadresse(no.nav.registre.testnorge.libs.avro.organisasjon.Forretningsadresse forretningsadresse) {
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