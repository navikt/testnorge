package no.nav.registre.frikort.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

public class JsonTestHelper {

    private JsonTestHelper() {

    }

    public static <T> void verifyPost(UrlPathPattern urlPathPattern, T requestBody, ObjectMapper objectMapper) throws JsonProcessingException {
        final String requestJsonBody = objectMapper.writeValueAsString(requestBody);
        verify(postRequestedFor(urlPathPattern).withRequestBody(equalToJson(requestJsonBody)));
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

}