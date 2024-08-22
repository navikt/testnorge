package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.PdlPersonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.PersonRequestDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
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

    private final PdlConsumer pdlConsumer;

    /**
     * Lager SokPersonVariabler som matcher filterene man vil basere søket på, og henter personer fra PDL som
     * oppfyller kravene. I tillegg filtreres vekk personer som er i bruk andre steder enn Testnorge
     *
     * @return En liste med identer for personene som matcher søk-variablene
     */

    public List<PdlPersonDTO.Person> getPersoner(PersonRequestDTO personRequest) {

        return pdlConsumer.getSokPerson(lagSokPersonPaging(personRequest),
                        lagSokPersonCriteria(personRequest),
                        PdlMiljoer.Q2)
                .map(PdlPersonDTO::getData)
                .filter(data -> nonNull(data.getSokPerson()))
                .map(PdlPersonDTO.Data::getSokPerson)
                .map(PdlPersonDTO.SokPerson::getHits)
                .flatMap(Flux::fromIterable)
                .map(PdlPersonDTO.Hit::getPerson)
                .collectList()
                .block();
    }

    private GraphqlVariables.Criteria lagSokPersonCriteria(PersonRequestDTO personRequest) {

        var searchRuleFoedselsdato = new java.util.HashMap<String, Object>();
        searchRuleFoedselsdato.put("from", personRequest.getFrom());
        searchRuleFoedselsdato.put("to", personRequest.getTo());

        GraphqlVariables.Filter filterBostedPostnr = GraphqlVariables.Filter.builder()
                .fieldName("person.bostedsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", personRequest.getPostnr()))
                .build();

        GraphqlVariables.Filter filterOppholdPostnr = GraphqlVariables.Filter.builder()
                .fieldName("person.oppholdsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", personRequest.getPostnr()))
                .build();

        GraphqlVariables.Filter filterFoedselsdato = GraphqlVariables.Filter.builder()
                .fieldName("person.foedselsdato.foedselsdato")
                .searchRule(searchRuleFoedselsdato)
                .build();

        GraphqlVariables.Filter random = GraphqlVariables.Filter.builder()
                .fieldName("random")
                .searchRule(Map.of("random", RANDOM.nextFloat()))
                .build();

        GraphqlVariables.Filter testnorge = GraphqlVariables.Filter.builder()
                .fieldName("tags")
                .searchRule(Map.of("contains", "TESTNORGE"))
                .build();

        GraphqlVariables.Filter dolly = GraphqlVariables.Filter.builder()
                .fieldName("tags")
                .searchRule(Map.of("contains", "DOLLY"))
                .build();

        var or = Map.of("or", List.of(filterBostedPostnr, filterOppholdPostnr));
        var not = Map.of("not", List.of(dolly));
        var and = List.of(testnorge, or, not, filterFoedselsdato, random);

        return GraphqlVariables.Criteria.builder().and(and).build();
    }

    private GraphqlVariables.Paging lagSokPersonPaging(PersonRequestDTO personRequest) {

        return GraphqlVariables.Paging.builder()
                .pageNumber(0)
                .resultsPerPage(personRequest.getResultsPerPage())
                .build();
    }
}
