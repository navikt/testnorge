package no.nav.dolly.web.provider.web;

import static java.lang.String.format;
import static no.nav.dolly.web.domain.TemaGrunnlag.GEN;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
//import com.google.common.io.CharStreams;

import org.apache.http.Consts;
import org.springframework.core.codec.EncodingException;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.web.domain.GraphQLRequest;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyService {

    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String TEMA = "Tema";
    private final RestTemplate proxyRestTemplate;

    public ResponseEntity proxyRequest(
            String body,
            HttpMethod method,
            HttpServletRequest request,
            String requestUrl) {

        HttpHeaders headers = copyHeaders(request);

        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getIdToken());

        if (headers.get(NAV_CALL_ID) == null) {
            headers.add(NAV_CALL_ID, String.valueOf(UUID.randomUUID()));
        }
        if (headers.get(NAV_CONSUMER_ID) == null) {
            headers.add(NAV_CONSUMER_ID, "dolly-proxy");
        }
        headers.add(CONTENT_TYPE, "application/json; charset=UTF-8");
        headers.set(HttpHeaders.ACCEPT_ENCODING, "identity=1.0");

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        try {
            return proxyRestTemplate.exchange(decodeUrl(requestUrl), method, httpEntity, String.class);

        } catch (HttpClientErrorException exception) {
            return ResponseEntity.status(exception.getStatusCode())
                    .headers(exception.getResponseHeaders())
                    .body(exception.getResponseBodyAsString());
        }
    }
//
//    public ResponseEntity pdlPersonRequest(
//            String requestUrl, String ident) {
//
//        Map<String, Object> variables = new HashMap();
//        variables.put("ident", ident);
//        variables.put("historikk", true);
//
//        String query = null;
//        InputStream queryStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("pdlperson/pdlquery.graphql");
//        try {
//            Reader reader = new InputStreamReader(queryStream, Consts.UTF_8);
//            query = CharStreams.toString(reader);
//        } catch (IOException e) {
//            log.error("Lesing av query ressurs feilet");
//        }
//
//        GraphQLRequest graphQLRequest = GraphQLRequest.builder()
//                .query(query)
//                .variables(variables)
//                .build();
//
//        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
//
//        try {
//            return proxyRestTemplate.exchange(RequestEntity.post(
//                    URI.create(requestUrl))
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getIdToken())
//                    .header(NAV_CALL_ID, String.valueOf(UUID.randomUUID()))
//                    .header(NAV_CONSUMER_ID, "dolly-frontend")
//                    .header(TEMA, GEN.name())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(graphQLRequest), JsonNode.class);
//
//        } catch (HttpClientErrorException exception) {
//            return ResponseEntity.status(exception.getStatusCode())
//                    .headers(exception.getResponseHeaders())
//                    .body(exception.getResponseBodyAsString());
//        }
//    }

    private String decodeUrl(String requestUrl) {
        try {
            return URLDecoder.decode(requestUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EncodingException(format("Encoding av requesturl %s feilet", requestUrl));
        }
    }

    private HttpHeaders copyHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if ("connection".equals(headerName)) {
                headers.set(headerName, "keep-alive");
            } else if ("Cookie".equals(headerName)) {
                headers.set(headerName, request.getHeader(headerName));
            } else {
                headers.set(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }

    private Cookie getIdTokenCookie(HttpServletRequest request) {
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals("ID_token")) {
                return c;
            }
        }
        return null;
    }
}
