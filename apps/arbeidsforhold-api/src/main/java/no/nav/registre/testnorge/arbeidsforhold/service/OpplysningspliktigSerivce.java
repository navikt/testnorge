package no.nav.registre.testnorge.arbeidsforhold.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.consumer.AaregConsumer;
import no.nav.registre.testnorge.arbeidsforhold.consumer.OrganisasjonConsumer;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforhold.domain.Opplysningspliktig;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpplysningspliktigSerivce {
    private final OrganisasjonConsumer organisasjonConsumer;
    private final AaregConsumer aaregConsumer;

    public Opplysningspliktig getOpplysningspliktig(String opplysningspliktig, String miljo) {
        var organisasjon = organisasjonConsumer.getOrganisjon(opplysningspliktig, miljo);
        log.info("Opplysningspliktig driver {} virksomhenter.", organisasjon.getDriverVirksomheter().size());
        Map<String, List<Arbeidsforhold>> map = organisasjon.getDriverVirksomheter()
                .stream()
                .collect(Collectors.toMap(
                        orgnummer -> orgnummer,
                        orgnummer -> aaregConsumer.getArbeidsforholdByArbeidsgiver(orgnummer, miljo)
                ));
        return new Opplysningspliktig(opplysningspliktig, map);
    }

}
