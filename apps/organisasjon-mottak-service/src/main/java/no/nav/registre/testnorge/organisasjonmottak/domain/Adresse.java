package no.nav.registre.testnorge.organisasjonmottak.domain;

public abstract class Adresse extends ToLine {
    private final String postnummer;
    private final String landkode;
    private final String kommunenummer;
    private final String poststed;
    private final String postadresse1;
    private final String postadresse2;
    private final String postadresse3;
    private final String linjenummer;
    private final String vegadresseId;

    public Adresse(String postnummer, String landkode, String kommunenummer, String poststed, String postadresse1, String postadresse2, String postadresse3, String linjenummer, String vegadresseId) {
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

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder(getFelttype(), 185)
                .append(8, postnummer)
                .append(17, landkode)
                .append(20, kommunenummer)
                .append(29, poststed)
                .append(64, postadresse1)
                .append(99, postadresse2)
                .append(134, postadresse3)
                .append(169, linjenummer)
                .append(170, vegadresseId);
    }

    public abstract String getFelttype();
}
