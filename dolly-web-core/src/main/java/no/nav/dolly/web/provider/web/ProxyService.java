package no.nav.dolly.web.provider.web;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import org.springframework.core.codec.EncodingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProxyService {

    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String CONTENT_TYPE = "Content-Type";
    private final RestTemplate proxyRestTemplate;

    public ResponseEntity proxyRequest(
            String body,
            HttpMethod method,
            HttpServletRequest request,
            String requestUrl) {

        HttpHeaders headers = copyHeaders(request);

        //TODO Brukes når Dolly tar imot token i header
        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getIdToken());

        headers.add(NAV_CALL_ID, String.valueOf(UUID.randomUUID()));
        headers.add(NAV_CONSUMER_ID, "dolly-proxy");
        headers.add(CONTENT_TYPE, "application/json");

        // Decoding exception occurs when accepted-encoding: "deflate, gzip and br" are used at the same time
        headers.set(HttpHeaders.ACCEPT_ENCODING, "br;q=1.0, gzip;q=0.5, *;q=0.1");

        // TODO Brukes imot eksisterende dolly som forventer cookie i header
        Cookie idTokenCookie = getIdTokenCookie(request);
        if (idTokenCookie != null) {
            headers.add(HttpHeaders.COOKIE, idTokenCookie.getName() + "=" + idTokenCookie.getValue());
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        try {
            return proxyRestTemplate.exchange(decodeUrl(requestUrl), method, httpEntity, String.class);

        } catch (HttpClientErrorException exception) {
            return ResponseEntity.status(exception.getStatusCode())
                    .headers(exception.getResponseHeaders())
                    .body(exception.getResponseBodyAsString());
        }
    }

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
