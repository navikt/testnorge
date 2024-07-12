package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphqlTestJson {

    private final PdlConsumer pdlConsumer;

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws JsonProcessingException {
        log.info("Er i test");

        Map<String, String> searchRuleVerdier = new java.util.HashMap<>();
        searchRuleVerdier.put("from", "1955");
        searchRuleVerdier.put("to", "2006");

        GraphqlVariables.Filter filter1 = GraphqlVariables.Filter.builder()
                .fieldName("person.bostedsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", "1???"))
                .build();

        GraphqlVariables.Filter filter2 = GraphqlVariables.Filter.builder()
                .fieldName("person.oppholdsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", "1???"))
                .build();

        GraphqlVariables.Filter filter3 = GraphqlVariables.Filter.builder()
                .fieldName("person.foedselsdato.foedselsaar")
                .searchRule(searchRuleVerdier)
                .build();

        Map<String, List<GraphqlVariables.Filter>> or = Map.of("or", List.of(filter1, filter2));
        List<Object> and = List.of(or, filter3);

        GraphqlVariables.Criteria criteria = GraphqlVariables.Criteria.builder().and(and).build();
        GraphqlVariables.Paging paging = GraphqlVariables.Paging.builder()
                .pageNumber(1)
                .resultsPerPage(5)
                .build();

        GraphqlVariables.Root rootTest = GraphqlVariables.Root.builder()
                .criteria(criteria)
                .paging(paging)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rootTest);

        log.info(jsonString);

        JsonNode node = pdlConsumer.getSokPerson(paging, criteria, PdlMiljoer.Q2).block();

        log.info("{}", node);
    }

}
