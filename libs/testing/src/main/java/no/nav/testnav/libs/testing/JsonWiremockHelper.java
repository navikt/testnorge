package no.nav.testnav.libs.testing;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

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

    public JsonWiremockHelper withResponseBody(Object responseBody) {
        this.responseBody = mapper.writeValueAsString(responseBody);
        return this;
    }

    public JsonWiremockHelper withRequestBody(Object requestBody, String... fieldsToIgnore) {
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