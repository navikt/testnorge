package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.adapter.TpsIdentAdapter;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Opplysningspliktig;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;

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
                .map(OppsummeringsdokumentDTO::getVirksomheter)
                .flatMap(Collection::stream)
                .map(VirksomhetDTO::getPersoner)
                .flatMap(Collection::stream)
                .map(PersonDTO::getIdent)
                .collect(Collectors.toSet());
        log.info("Fant {} identer i {}.", identer.size(), miljo);
        return identer;
    }

    public Set<String> getIdenterUtenArbeidsforhold(String miljo, int max) {
        var identer = tpsIdentAdapter.getIdenter(miljo, max);

        var identerMedArbeidsforhold = getIdenterMedArbeidsforhold(miljo);
        var identerUtenArbeidsforhold = identer
                .parallelStream()
                .filter(ident -> !identerMedArbeidsforhold.contains(ident))
                .limit(max)
                .collect(Collectors.toSet());

        if (identerUtenArbeidsforhold.isEmpty()) {
            log.warn("Prøvde å finne {} identer men fant ingen uten arbeidsforhold. Prøv å øke antall personer som kan ha arbeidsforhold i Mini-Norge.", max);
        } else {
            log.info("Fant {}/{} identer uten arbeidsforhold i {}.", identerUtenArbeidsforhold.size(), max, miljo);
        }
        return identerUtenArbeidsforhold;
    }

}
