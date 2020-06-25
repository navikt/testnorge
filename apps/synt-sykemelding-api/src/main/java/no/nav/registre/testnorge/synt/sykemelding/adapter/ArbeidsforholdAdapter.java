package no.nav.registre.testnorge.synt.sykemelding.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.synt.sykemelding.consumer.ArbeidsforholdConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.OrganisasjonConsumer;
import no.nav.registre.testnorge.synt.sykemelding.domain.Arbeidsforhold;

@Component
@RequiredArgsConstructor
public class ArbeidsforholdAdapter {
    private final ArbeidsforholdConsumer arbeidsforholdConsumer;
    private final OrganisasjonConsumer organisasjonConsumer;

    public Arbeidsforhold getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        return new Arbeidsforhold(
                arbeidsforholdConsumer.getArbeidsforhold(ident, orgnummer, arbeidsforholdId),
                organisasjonConsumer.getOrganisasjon(orgnummer)
        );
    }

}
