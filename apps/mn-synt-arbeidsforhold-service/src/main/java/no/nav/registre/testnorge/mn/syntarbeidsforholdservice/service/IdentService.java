package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.VirksomhetDTO;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.ArbeidsforholdConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {
    private final ArbeidsforholdConsumer arbeidsforholdConsumer;

    public Set<String> getIdenterMedArbeidsforhold(String mijlo) {
        return arbeidsforholdConsumer
                .getAlleOpplysningspliktig(mijlo)
                .stream()
                .map(Opplysningspliktig::getDriverVirksomhenter)
                .flatMap(Collection::stream)
                .map(VirksomhetDTO::getPersoner)
                .flatMap(Collection::stream)
                .map(PersonDTO::getIdent)
                .collect(Collectors.toSet());
    }

    public Set<String> getIdenterUtenArbeidsforhold(String mijlo, int max) {
        var identer = Set.of(
                "29099625137",
                "22099625698",
                "03068117630",
                "30039123645",
                "20039121582",
                "04028420728",
                "26129222874",
                "10089120697",
                "01038514438",
                "27107527635",
                "16046521696",
                "23086700166",
                "12089022817",
                "21048716846",
                "15087021499",
                "05017920652",
                "21059020525",
                "19088122359",
                "18078222936",
                "21097216530",
                "11108700191",
                "30128420580",
                "14107324063",
                "09127322128",
                "18088527603",
                "29018620888",
                "25106323685",
                "24117921235",
                "11078723760",
                "08069120467"
        );

        var identerMedArbeidsforhold = getIdenterMedArbeidsforhold(mijlo);
        var identerUtenArbeidsforhold = identer
                .stream()
                .filter(ident -> !identerMedArbeidsforhold.contains(ident))
                .collect(Collectors.toSet());
        if (identerUtenArbeidsforhold.isEmpty()) {
            log.warn("Prøved å finne {} identer men fant ingen uten arbeidsforhold. Prøv å øke antall personer som kan ha arbeidsforhold i Mini-Norge.", max);
        } else {
            log.info("Fant {}/{} identer uten arbeidsforhold i {}.", identerUtenArbeidsforhold.size(), max, mijlo);
        }
        return identerUtenArbeidsforhold;
    }

}
