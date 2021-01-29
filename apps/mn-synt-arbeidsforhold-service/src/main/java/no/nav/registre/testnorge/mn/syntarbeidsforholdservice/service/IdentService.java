package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.VirksomhetDTO;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {
    private final OppsummeringsdokumentConsumer arbeidsforholdConsumer;

    public Set<String> getIdenterMedArbeidsforhold(String miljo) {
        return arbeidsforholdConsumer
                .getAlleOpplysningspliktig(miljo)
                .stream()
                .map(Opplysningspliktig::getDriverVirksomheter)
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
                "08069120467",
                "16067500109",
                "20117020275",
                "29118520562",
                "24088022676",
                "01097221827",
                "17017920197",
                "14088221843",
                "29078819705",
                "14077824201",
                "18109021551",
                "07086521967",
                "31059018496",
                "27108620191",
                "19108219060",
                "28058917887",
                "22016421387",
                "21028221786",
                "09127800186",
                "07058819705",
                "02057722684"
        );

        var identerMedArbeidsforhold = getIdenterMedArbeidsforhold(mijlo);
        var identerUtenArbeidsforhold = identer
                .stream()
                .filter(ident -> !identerMedArbeidsforhold.contains(ident))
                .limit(max)
                .collect(Collectors.toSet());

        if (identerUtenArbeidsforhold.isEmpty()) {
            log.warn("Prøvde å finne {} identer men fant ingen uten arbeidsforhold. Prøv å øke antall personer som kan ha arbeidsforhold i Mini-Norge.", max);
        } else {
            log.info("Fant {}/{} identer uten arbeidsforhold i {}.", identerUtenArbeidsforhold.size(), max, mijlo);
        }
        return identerUtenArbeidsforhold;
    }

}
