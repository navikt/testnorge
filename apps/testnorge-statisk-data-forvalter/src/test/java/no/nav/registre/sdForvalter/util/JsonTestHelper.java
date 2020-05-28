package no.nav.registre.sdForvalter.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

public class JsonTestHelper {

    private JsonTestHelper() {

    }

    public static <T> void verifyPost(UrlPathPattern urlPathPattern, T requestBody, ObjectMapper objectMapper) throws Exception {
        verifyPost(urlPathPattern, requestBody, objectMapper, WireMock::equalToJson);
    }

    public static <T> void verifyPost(UrlPathPattern urlPathPattern, T requestBody, ObjectMapper objectMapper, String... fieldToIgnore) throws Exception {
        verifyPost(
                urlPathPattern,
                requestBody,
                objectMapper,
                value -> WireMock.matching(convertToRegexString(value, fieldToIgnore))
        );
    }

    private static <T> void verifyPost(UrlPathPattern urlPathPattern, T requestBody, ObjectMapper objectMapper, StringValuePatternOperation operation) throws Exception {
        final String body = objectMapper.writeValueAsString(requestBody);
        verify(postRequestedFor(urlPathPattern).withRequestBody(operation.getPattern(body)));
    }


    public static <T> void verifyPost(UrlPathPattern urlPathPattern) {
        verify(postRequestedFor(urlPathPattern));
    }

    public static <T, K> void stubPost(UrlPathPattern urlPathPattern, T requestBody, K responseBody, ObjectMapper objectMapper) throws Exception {
        final String requestJsonBody = objectMapper.writeValueAsString(requestBody);
        final String responseJsonBody = objectMapper.writeValueAsString(responseBody);
        stubFor(post(urlPathPattern)
                .withRequestBody(equalToJson(requestJsonBody))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJsonBody)
                )
        );
    }

    public static <T> void stubPost(UrlPathPattern urlPathPattern, T responseBody, ObjectMapper objectMapper) throws Exception {
        final String responseJsonBody = objectMapper.writeValueAsString(responseBody);
        stubFor(post(urlPathPattern)
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJsonBody)
                )
        );
    }

    public static <T> void stubPost(UrlPathPattern urlPathPattern) {
        stubFor(post(urlPathPattern));
    }


    public static <T, K> void stubGet(UrlPathPattern urlPathPattern, K responseBody, ObjectMapper objectMapper) throws Exception {
        final String responseJsonBody = objectMapper.writeValueAsString(responseBody);
        stubFor(get(urlPathPattern)
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJsonBody)
                )
        );
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
