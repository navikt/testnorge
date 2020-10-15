package no.nav.dolly.web.provider.web;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import org.springframework.core.codec.EncodingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    public <T> ResponseEntity proxyRequest(
            String body,
            HttpMethod method,
            HttpHeaders headers,
            String requestUrl,
            Class<T> returnClazz) {

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
            return proxyRestTemplate.exchange(decodeUrl(requestUrl), method, httpEntity, returnClazz);

        } catch (HttpClientErrorException exception) {
            return ResponseEntity.status(exception.getStatusCode())
                    .headers(exception.getResponseHeaders())
                    .body(exception.getResponseBodyAsString());
        }
    }


    public ResponseEntity proxyRequest(
            String body,
            HttpMethod method,
            HttpHeaders headers,
            String requestUrl) {
        return proxyRequest(body, method, headers, requestUrl, String.class);
    }


    private String decodeUrl(String requestUrl) {
        try {
            return URLDecoder.decode(requestUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EncodingException(format("Encoding av requesturl %s feilet", requestUrl));
        }
    }

    public HttpHeaders copyHeaders(HttpServletRequest request) {
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
}