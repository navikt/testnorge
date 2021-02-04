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
                "02047011569",
                "02057722684",
                "02068721132",
                "02088022494",
                "03068917769",
                "03108818378",
                "04049221164",
                "05017920652",
                "06017517048",
                "06037120705",
                "06099725875",
                "06118900128",
                "06127523108",
                "07048423277",
                "07058819705",
                "07086521967",
                "08077322362",
                "09099020374",
                "09127322128",
                "09127800186",
                "10068819046",
                "11066300163",
                "11078723760",
                "11086820325",
                "11108700191",
                "11117824707",
                "12070050003",
                "12089022817",
                "13059400147",
                "13077722836",
                "13109826086",
                "13126323101",
                "14077824201",
                "14088221843",
                "14107324063",
                "15087021499",
                "16046521696",
                "16067500109",
                "16068320201",
                "16117522214",
                "17017920197",
                "17029626103",
                "17038317458",
                "17109600113",
                "18078222936",
                "18088527603",
                "18109021551",
                "19017523824",
                "19078320047",
                "19088122359",
                "19108219060",
                "19126323045",
                "20039925703",
                "20117020275",
                "21028221786",
                "21029322357",
                "21048716846",
                "21059020525",
                "21097216530",
                "22016421387",
                "22028317887",
                "22107522682",
                "23058720925",
                "23086700166",
                "24069021316",
                "24069121922",
                "24088022676",
                "24117921235",
                "24128122180",
                "25059417803",
                "25088619636",
                "25089625362",
                "25106323685",
                "25119523772",
                "25128122896",
                "26057922442",
                "26107222404",
                "27107527635",
                "27108620191",
                "28038319207",
                "28058917887",
                "28118620625",
                "29017222110",
                "29018620888",
                "29036422538",
                "29066823294",
                "29069625752",
                "29078819705",
                "29106224232",
                "29118520562",
                "30037400145",
                "30116921312",
                "30128218005",
                "30128218358",
                "30128420580",
                "31059018496",
                "31078818304",
                "31089019845"
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
