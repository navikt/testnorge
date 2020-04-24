package no.nav.registre.ereg.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

public class JsonTestHelper {

    private JsonTestHelper() {

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
