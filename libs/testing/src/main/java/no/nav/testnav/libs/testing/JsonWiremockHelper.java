package no.nav.testnav.libs.testing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class JsonWiremockHelper {

    private final ObjectMapper mapper;
    private final Set<String> requestFieldsToIgnore = new HashSet<>();
    private final Map<String, String> queryParamMap = new HashMap<>();
    private UrlPathPattern urlPathPattern;
    private String requestBody;
    private String responseBody;

    private JsonWiremockHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonWiremockHelper withUrlPathMatching(String urlPathMatching) {
        this.urlPathPattern = urlPathMatching(urlPathMatching);
        return this;
    }

    public JsonWiremockHelper withQueryParam(String name, String value) {
        queryParamMap.put(name, value);
        return this;
    }

    public JsonWiremockHelper withResponseBody(Object responseBody) throws JsonProcessingException {
        this.responseBody = mapper.writeValueAsString(responseBody);
        return this;
    }

    public JsonWiremockHelper withRequestBody(Object requestBody, String... fieldsToIgnore) throws JsonProcessingException {
        this.requestBody = mapper.writeValueAsString(requestBody);
        requestFieldsToIgnore.addAll(Arrays.asList(fieldsToIgnore));
        return this;
    }

    public void stubPost(HttpStatus status) {
        stubFor(
                updateMappingBuilder(
                        post(urlPathPattern)
                                .willReturn(responseDefinition().withStatus(status.value()))
                )
        );
    }

    public void stubGet() {
        MappingBuilder mappingBuilder = get(urlPathPattern);

        queryParamMap.forEach((name, value) -> mappingBuilder.withQueryParam(name, equalTo(value)));

        if (responseBody != null) {
            mappingBuilder.willReturn(aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(responseBody)
            );
        }
        stubFor(mappingBuilder);
    }

    public void verifyGet() {
        RequestPatternBuilder requestPatternBuilder = getRequestedFor(urlPathPattern);
        verify(requestPatternBuilder);
    }

    public void verifyPost() {
        RequestPatternBuilder requestPatternBuilder = postRequestedFor(urlPathPattern);
        if (requestBody != null) {

            if (requestFieldsToIgnore.isEmpty()) {
                requestPatternBuilder.withRequestBody(equalToJson(requestBody));
            } else {
                requestPatternBuilder.withRequestBody(matching(convertToRegexString(
                        requestBody,
                        requestFieldsToIgnore.toArray(String[]::new)
                )));
            }

        }
        verify(requestPatternBuilder);
    }

    public static JsonWiremockHelper builder(ObjectMapper mapper) {
        return new JsonWiremockHelper(mapper);
    }

    private MappingBuilder updateMappingBuilder(MappingBuilder mappingBuilder) {
        queryParamMap.forEach((name, value) -> mappingBuilder.withQueryParam(name, equalTo(value)));

        if (requestBody != null) {
            mappingBuilder.withRequestBody(equalToJson(requestBody));
        }

        if (responseBody != null) {
            mappingBuilder.willReturn(aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(responseBody)
            );
        }
        return mappingBuilder;
    }

    private static String convertToRegexString(final String json, String... fieldToIgnore) {
        return ignoreFields(
                json
                        .replace("{", "\\{")
                        .replace("}", "\\}"),
                fieldToIgnore
        );
    }

    private static String ignoreFields(final String json, String... fieldsToIgnore) {
        String regex = json;
        for (String ignored : fieldsToIgnore) {
            regex = regex.replaceAll("\"" + ignored + "\":(\"([^\"]*)\"|null|true|false|\\d+)", "\"" + ignored + "\":\".*\"");
        }
        return regex;
    }
}