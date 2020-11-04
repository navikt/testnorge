package no.nav.registre.testnorge.libs.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonWiremockHelper {

    private final ObjectMapper mapper;

    private UrlPathPattern urlPathPattern;
    private String requestBody;
    private String responseBody;
    private Set<String> requestFieldsToIgnore = new HashSet<>();
    private Map<String, String> queryParamMap = new HashMap<>();

    private JsonWiremockHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static JsonWiremockHelper builder(ObjectMapper mapper) {
        return new JsonWiremockHelper(mapper);
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

    public void stubPost() {
        MappingBuilder mappingBuilder = post(urlPathPattern);

        updateMappingBuilder(mappingBuilder);

        stubFor(mappingBuilder);
    }


    public void stubPut() {
        MappingBuilder mappingBuilder = put(urlPathPattern);

        updateMappingBuilder(mappingBuilder);

        stubFor(mappingBuilder);
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

    public void stubDelete() {
        MappingBuilder mappingBuilder = delete(urlPathPattern);

        updateMappingBuilder(mappingBuilder);
        stubFor(mappingBuilder);
    }

    private void updateMappingBuilder(MappingBuilder mappingBuilder) {
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
    }

    public void verifyDelete() {
        RequestPatternBuilder requestPatternBuilder = deleteRequestedFor(urlPathPattern);
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

    private static String convertToRegexString(final String value, String... fieldToIgnore) {
        return ignoreFields(
                value
                        .replaceAll("\\{", "\\\\{")
                        .replaceAll("\\}", "\\\\}"),
                fieldToIgnore
        );
    }

    private static String ignoreFields(final String property, String... fieldToIgnore) {
        String regex = property;
        for (String ignored : fieldToIgnore) {
            regex = regex.replaceAll("\"" + ignored + "\":(\"([^\"]*)\"|null|true|false|\\d+)", "\"" + ignored + "\":\".*\"");
        }
        return regex;
    }
}