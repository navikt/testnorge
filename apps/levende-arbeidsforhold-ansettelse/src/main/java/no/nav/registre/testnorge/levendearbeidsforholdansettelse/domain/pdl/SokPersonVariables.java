package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SokPersonVariables {

    //private GraphqlVariables variables
    private int pageNumber;
    private int resultsPerPage;
    private String from;
    private String to;
    private String postNr;

    public GraphqlVariables.Criteria lagSokPersonCriteria() {
        Map<String, String> searchRuleVerdier = new java.util.HashMap<>();
        searchRuleVerdier.put("from", from);
        searchRuleVerdier.put("to", to );

        GraphqlVariables.Filter filter1 = GraphqlVariables.Filter.builder()
                .fieldName("person.bostedsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", postNr))
                .build();

        GraphqlVariables.Filter filter2 = GraphqlVariables.Filter.builder()
                .fieldName("person.oppholdsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", postNr))
                .build();

        GraphqlVariables.Filter filter3 = GraphqlVariables.Filter.builder()
                .fieldName("person.foedselsdato.foedselsaar")
                .searchRule(searchRuleVerdier)
                .build();

        Map<String, List<GraphqlVariables.Filter>> or = Map.of("or", List.of(filter1, filter2));
        List<Object> and = List.of(or, filter3);

        return GraphqlVariables.Criteria.builder().and(and).build();


    }

    public GraphqlVariables.Paging lagSokPersonPaging() {
        return GraphqlVariables.Paging.builder()
                .pageNumber(pageNumber)
                .resultsPerPage(resultsPerPage)
                .build();
    }

}
