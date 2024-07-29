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

    private int pageNumber;
    private int resultsPerPage;
    private String from;
    private String to;
    private String postnr;
    private final String identRegex = "\\w+[0-9]{2}[8-9]{1}[0-9]{5}\\w+";

    public GraphqlVariables.Criteria lagSokPersonCriteria() {

        Map<String, String> searchRuleFoedselsaar = new java.util.HashMap<>();
        searchRuleFoedselsaar.put("from", from);
        searchRuleFoedselsaar.put("to", to );

        GraphqlVariables.Filter filterBostedPostnr = GraphqlVariables.Filter.builder()
                .fieldName("person.bostedsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", postnr))
                .build();

        GraphqlVariables.Filter filterOppholdPostnr = GraphqlVariables.Filter.builder()
                .fieldName("person.oppholdsadresse.vegadresse.postnummer")
                .searchRule(Map.of("wildcard", postnr))
                .build();

        GraphqlVariables.Filter filterFoedselsaar = GraphqlVariables.Filter.builder()
                .fieldName("person.foedselsdato.foedselsaar")
                .searchRule(searchRuleFoedselsaar)
                .build();

        GraphqlVariables.Filter filterIdent = GraphqlVariables.Filter.builder()
                .fieldName("identer.ident")
                .searchRule(Map.of("wildcard", identRegex))
                .build();

        Map<String, List<GraphqlVariables.Filter>> or = Map.of("or", List.of(filterBostedPostnr, filterOppholdPostnr));
        List<Object> and = List.of(or, filterFoedselsaar, filterIdent);

        return GraphqlVariables.Criteria.builder().and(and).build();
    }

    public GraphqlVariables.Paging lagSokPersonPaging() {
        return GraphqlVariables.Paging.builder()
                .pageNumber(pageNumber)
                .resultsPerPage(resultsPerPage)
                .build();
    }
}