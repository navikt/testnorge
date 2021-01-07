package no.nav.registre.testnorge.organisasjonmottak.domain;


public class Formaal extends ToLine {
    private final String formaal;

    public Formaal(no.nav.registre.testnorge.libs.avro.organisasjon.v1.Formaal formaal) {
        this.formaal = formaal.getFormaal();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("FORM", 9)
                .append(8, formaal);
    }

}
