package no.nav.testnav.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.DatoIntervall;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.PdlPersonDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.testnav.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlService {

    private static final Random RANDOM = new SecureRandom();
    private static final String CONTAINS = "contains";

    private final PdlConsumer pdlConsumer;

    /**
     * Lager SokPersonVariabler som matcher filterene man vil basere søket på, og henter personer fra PDL som
     * oppfyller kravene. I tillegg filtreres vekk personer som er i bruk andre steder enn Testnorge
     *
     * @return En liste med identer for personene som matcher søk-variablene
     */

    public Flux<PdlPersonDTO.Person> getPerson(DatoIntervall datoIntervall, String postnummer) {

        return pdlConsumer.getSokPerson(lagSokPersonPaging(1),
                        lagSokPersonCriteria(datoIntervall, postnummer),
                        PdlMiljoer.Q2)
                .filter(data -> nonNull(data.getData()))
                .map(PdlPersonDTO::getData)
                .filter(data -> nonNull(data.getSokPerson()))
                .map(PdlPersonDTO.Data::getSokPerson)
                .map(PdlPersonDTO.SokPerson::getHits)
                .flatMap(Flux::fromIterable)
                .map(PdlPersonDTO.Hit::getPerson);
    }

    private GraphqlVariables.Criteria lagSokPersonCriteria(DatoIntervall intervall, String postnummer) {

        GraphqlVariables.Filter filterBostedPostnr = GraphqlVariables.Filter.builder()
                .fieldName("person.bostedsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", wildcharPostnummer(postnummer)))
                .build();

        GraphqlVariables.Filter filterOppholdPostnr = GraphqlVariables.Filter.builder()
                .fieldName("person.oppholdsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", wildcharPostnummer(postnummer)))
                .build();

        GraphqlVariables.Filter filterFoedselsdato = GraphqlVariables.Filter.builder()
                .fieldName("person.foedselsdato.foedselsdato")
                .searchRule(Map.of("from", intervall.getFom(),
                        "to", intervall.getTom()))
                .build();

        GraphqlVariables.Filter random = GraphqlVariables.Filter.builder()
                .fieldName("random")
                .searchRule(Map.of("random", RANDOM.nextFloat()))
                .build();

        GraphqlVariables.Filter testnorge = GraphqlVariables.Filter.builder()
                .fieldName("tags")
                .searchRule(Map.of(CONTAINS, "TESTNORGE"))
                .build();

        GraphqlVariables.Filter dolly = GraphqlVariables.Filter.builder()
                .fieldName("tags")
                .searchRule(Map.of(CONTAINS, "DOLLY"))
                .build();

        GraphqlVariables.Filter arenasynt = GraphqlVariables.Filter.builder()
                .fieldName("tags")
                .searchRule(Map.of(CONTAINS, "ARENASYNT"))
                .build();

        var or = Map.of("or", List.of(filterBostedPostnr, filterOppholdPostnr));
        var not = Map.of("not", List.of(dolly, arenasynt));
        var and = List.of(testnorge, not, or, filterFoedselsdato, random);

        return GraphqlVariables.Criteria.builder().and(and).build();
    }

    private GraphqlVariables.Paging lagSokPersonPaging(Integer antall) {

        return GraphqlVariables.Paging.builder()
                .pageNumber(0)
                .resultsPerPage(antall)
                .build();
    }

    private static String wildcharPostnummer(String postnummer) {

        return postnummer.charAt(0) + "???";
    }
}
