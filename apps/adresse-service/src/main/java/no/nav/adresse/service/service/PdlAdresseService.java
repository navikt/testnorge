package no.nav.adresse.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.adresse.service.consumer.PdlAdresseConsumer;
import no.nav.adresse.service.dto.AdresseRequest;
import no.nav.adresse.service.dto.GraphQLRequest;
import org.apache.http.Consts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static no.nav.adresse.service.dto.GraphQLRequest.SearchRule.equals;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlAdresseService {

    private static final String PDL_ADRESSE_QUERY = "pdladresse/pdlquery.graphql";

    private final PdlAdresseConsumer pdlAdresseConsumer;

    private static String getQueryFromFile(String pathResource) {

        val resource = new ClassPathResource(pathResource);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Consts.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", pathResource, e);
            return null;
        }
    }

    public JsonNode getAdressePostnummer(String postnummer) {

        return pdlAdresseConsumer.sendPdlAdresseSoek(GraphQLRequest.builder()
                .query(getQueryFromFile(PDL_ADRESSE_QUERY))
                .variables(Map.of(
                        "paging", GraphQLRequest.Paging.builder()
                                .pageNumber("1")
                                .resultsPerPage("30")
                                .sortBy(Map.of(GraphQLRequest.SortBy.fieldName, "adressenavn",
                                        GraphQLRequest.SortBy.direction, "ASC"))
                                .build(),
                        "criteria", singletonList(GraphQLRequest.Criteria.builder()
                                .fieldName("postnummer")
                                .searchRule(Map.of(equals, postnummer))
                                .build())))
                .build());
    }

    public JsonNode getAdresseKommunenummer(String kommunenummer, String bydelsnavn) {
//
//        GraphQLRequest.Criteria criteria = nonNull(bydelsnavn) ?
//        GraphQLRequest.Criteria.builder()
//                .fieldName("kommunenummer")
//                .searchRule(Map)
//        Map.of(GraphQLRequest.Criteria.fieldName, kommunenummerm )

//        return pdlAdresseConsumer.sendPdlAdresseSoek(pdlAdresseConsumer.sendPdlAdresseSoek(GraphQLRequest.builder()
//                .query(getQueryFromFile(PDL_ADRESSE_QUERY))
//                .variables(Map.of(
//                        "paging", GraphQLRequest.Paging.builder()
//                                .pageNumber("1")
//                                .resultsPerPage("30")
//                                .sortBy(Map.of(GraphQLRequest.SortBy.fieldName, "adressenavn",
//                                        GraphQLRequest.SortBy.direction, "ASC"))
//                                .build(),
//                        "criteria", GraphQLRequest.Criteria.builder()
//                                .fieldName("kommunenummer")
//                                .searchRule(Map.of(equals, kommunenummer,
//                                        equals, ))
//                                .build()))
//                .build());
        return null;
    }

    public JsonNode getAdresseAutoComplete(AdresseRequest request) {


        return null;
    }
}
