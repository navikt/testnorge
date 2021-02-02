package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;
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
                "01069625394",
                "01097221827",
                "02057722684",
                "03068917769",
                "05017920652",
                "06017517048",
                "06127523108",
                "07058819705",
                "07086521967",
                "09127322128",
                "09127800186",
                "11066300163",
                "11078723760",
                "11108700191",
                "11117824707",
                "12089022817",
                "14077824201",
                "14088221843",
                "14107324063",
                "15087021499",
                "16046521696",
                "16067500109",
                "17017920197",
                "17109600113",
                "18078222936",
                "18088527603",
                "18109021551",
                "19078320047",
                "19088122359",
                "19108219060",
                "19126323045",
                "20039925703",
                "20117020275",
                "21028221786",
                "21048716846",
                "21059020525",
                "21097216530",
                "22016421387",
                "22107522682",
                "23086700166",
                "24088022676",
                "24117921235",
                "25088619636",
                "25089625362",
                "25106323685",
                "27107527635",
                "27108620191",
                "28038319207",
                "28058917887",
                "29017222110",
                "29018620888",
                "29069625752",
                "29078819705",
                "29118520562",
                "30037400145",
                "30116921312",
                "30128218005",
                "30128218358",
                "30128420580",
                "31059018496"
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
