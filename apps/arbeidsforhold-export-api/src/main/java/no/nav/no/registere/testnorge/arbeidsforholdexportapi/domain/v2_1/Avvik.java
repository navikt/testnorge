package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.v2_1;

import lombok.Getter;

@Getter
public class Avvik implements no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Avvik {
    private final String id;
    private final String navn;
    private final String alvorlighetsgrad;
    private final String type;
    private final String arbeidsforholdType;

    public Avvik(no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Avvik avvik, String type, String arbeidsforholdType) {
        this.id = avvik.getId();
        this.navn = avvik.getNavn();
        this.alvorlighetsgrad = avvik.getAlvorlighetsgrad() != null ? avvik.getAlvorlighetsgrad().value() : null;
        this.type = type;
        this.arbeidsforholdType = arbeidsforholdType;
    }

    @Override
    public String getDetaljer() {
        return null;
    }
}
