package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon;

public class Knytning extends ToLine {
    private final String juridiskEnhet;
    private final String enhetstype;

    public Knytning(no.nav.registre.testnorge.libs.avro.organisasjon.v1.Knytning knytning, Organisasjon organisasjon) {
        enhetstype = organisasjon.getEnhetstype();
        juridiskEnhet = knytning.getJuridiskEnhet();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder(enhetstype, 66)
                .append(5, "SSY")
                .append(8, "K")
                .append(9, "D")
                .append(41, juridiskEnhet);
    }
}
