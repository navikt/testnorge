package no.nav.testnav.apps.syntsykemeldingapi.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.testnav.apps.syntsykemeldingapi.consumer.ArbeidsforholdConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.OrganisasjonConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Arbeidsforhold;

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
