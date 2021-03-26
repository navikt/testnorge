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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static no.nav.adresse.service.dto.GraphQLRequest.SearchRule.equals;
import static no.nav.adresse.service.dto.GraphQLRequest.SearchRule.fuzzy;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

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
                        "criteria", List.of(GraphQLRequest.Criteria.builder()
                                .fieldName("postnummer")
                                .searchRule(Map.of(equals, postnummer))
                                .build())))
                .build());
    }

    public JsonNode getAdresseKommunenummer(String kommunenummer, String bydelsnavn) {

        return pdlAdresseConsumer.sendPdlAdresseSoek(GraphQLRequest.builder()
                .query(getQueryFromFile(PDL_ADRESSE_QUERY))
                .variables(Map.of(
                        "paging", GraphQLRequest.Paging.builder()
                                .pageNumber("1")
                                .resultsPerPage("30")
                                .sortBy(Map.of(GraphQLRequest.SortBy.fieldName, "adressenavn",
                                        GraphQLRequest.SortBy.direction, "ASC"))
                                .build(),
                        "criteria",
                        List.of(
                                GraphQLRequest.Criteria.builder()
                                        .fieldName("kommunenummer")
                                        .searchRule(Map.of(equals, kommunenummer))
                                        .build(),
                                isNotBlank(bydelsnavn) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("bydelsnavn")
                                                .searchRule(Map.of(fuzzy, bydelsnavn))
                                                .build() : null)
                                .stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())))
                .build());
    }

    public JsonNode getAdresseAutoComplete(AdresseRequest request) {

        return pdlAdresseConsumer.sendPdlAdresseSoek(GraphQLRequest.builder()
                .query(getQueryFromFile(PDL_ADRESSE_QUERY))
                .variables(Map.of(
                        "paging", GraphQLRequest.Paging.builder()
                                .pageNumber("1")
                                .resultsPerPage("30")
                                .sortBy(Map.of(GraphQLRequest.SortBy.fieldName, "adressenavn",
                                        GraphQLRequest.SortBy.direction, "ASC"))
                                .build(),
                        "criteria",
                        List.of(
                                isNotBlank(request.getVeinavn()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("adressenavn")
                                                .searchRule(Map.of(fuzzy, request.getVeinavn()))
                                                .build() : null,
                                isNotBlank(request.getHusnummer()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("husnummer")
                                                .searchRule(Map.of(equals, request.getHusnummer()))
                                                .build() : null,
                                isNotBlank(request.getHusbokstav()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("husbokstav")
                                                .searchRule(Map.of(equals, request.getHusbokstav()))
                                                .build() : null,
                                isNotBlank(request.getPostnummer()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("postnummer")
                                                .searchRule(Map.of(equals, request.getPostnummer()))
                                                .build() : null,
                                isNotBlank(request.getPoststed()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("poststed")
                                                .searchRule(Map.of(fuzzy, request.getPoststed()))
                                                .build() : null,
                                isNotBlank(request.getKommunenummer()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("kommunenummer")
                                                .searchRule(Map.of(equals, request.getKommunenummer()))
                                                .build() : null,
                                isNotBlank(request.getKommunenavn()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("kommunenavn")
                                                .searchRule(Map.of(fuzzy, request.getKommunenavn()))
                                                .build() : null,
                                isNotBlank(request.getBydelsnummer()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("bydelsnummer")
                                                .searchRule(Map.of(equals, request.getBydelsnummer()))
                                                .build() : null,
                                isNotBlank(request.getBydelsnavn()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("bydelsnavn")
                                                .searchRule(Map.of(fuzzy, request.getBydelsnavn()))
                                                .build() : null,
                                isNotBlank(request.getTilleggsnavn()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("tilleggsnavn")
                                                .searchRule(Map.of(fuzzy, request.getTilleggsnavn()))
                                                .build() : null,
                                isNotBlank(request.getMatrikkelId()) ?
                                        GraphQLRequest.Criteria.builder()
                                                .fieldName("matrikkelId")
                                                .searchRule(Map.of(equals, request.getMatrikkelId()))
                                                .build() : null
                                )
                                .stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())))
                .build());
    }
}
