package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.PersonDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter.TpsIdentAdapter;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {
    private final OppsummeringsdokumentConsumer arbeidsforholdConsumer;
    private final TpsIdentAdapter tpsIdentAdapter;

    public Set<String> getIdenterMedArbeidsforhold(String miljo) {
        var identer = arbeidsforholdConsumer
                .getAlleOpplysningspliktig(miljo)
                .stream()
                .map(Opplysningspliktig::getDriverVirksomheter)
                .flatMap(Collection::stream)
                .map(VirksomhetDTO::getPersoner)
                .flatMap(Collection::stream)
                .map(PersonDTO::getIdent)
                .collect(Collectors.toSet());
        log.info("Fant {} identer i {}.", identer.size(), miljo);
        return identer;
    }

    public Flux<String> getIdenterUtenArbeidsforhold(String miljo, int max) {
        var identer = tpsIdentAdapter.getIdenter(miljo, max);

        var identerMedArbeidsforhold = getIdenterMedArbeidsforhold(miljo);


        return identer.filter(ident -> !identerMedArbeidsforhold.contains(ident));
    }

}
