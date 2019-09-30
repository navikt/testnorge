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
import java.net.URLEncoder;
import java.util.Enumeration;

@Service
@RequiredArgsConstructor
public class ProxyService {

    private final RestTemplate proxyRestTemplate;

    public ResponseEntity<String> proxyRequest(
            String body,
            HttpMethod method,
            HttpServletRequest request,
            String requestUrl) {

        HttpHeaders headers = copyHeaders(request);

        //TODO Brukes når Dolly tar imot token i header
        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getIdToken());

        Cookie idTokenCookie = getIdTokenCookie(request);
        if (idTokenCookie != null) {
            headers.add(HttpHeaders.COOKIE, idTokenCookie.getName() + "=" + idTokenCookie.getValue());
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        try {
            return proxyRestTemplate.exchange(encodeUrl(requestUrl), method, httpEntity, String.class);
        } catch (HttpClientErrorException exception) {
            return ResponseEntity.status(exception.getStatusCode())
                    .body(exception.getResponseBodyAsString());
        }
    }

    private String encodeUrl(String requestUrl) {
        try {
            return URLEncoder.encode(requestUrl, "UTF-8");
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
