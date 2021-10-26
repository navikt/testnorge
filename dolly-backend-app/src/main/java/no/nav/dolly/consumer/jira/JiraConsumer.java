package no.nav.dolly.consumer.jira;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.properties.JiraProps;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class JiraConsumer {

    private final JiraProps jiraProps;
    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> excuteRequest(String url, HttpMethod httpMethod, HttpEntity httpEntity, Class<T> responseClass) {
        return restTemplate.exchange(format("%s%s", getBaseUrl(), url), httpMethod, httpEntity, responseClass);
    }

    public HttpHeaders createHttpHeaders(MediaType mediaType) {
        return createHttpHeaders(mediaType, null);
    }

    public HttpHeaders createHttpHeaders(MediaType mediaType, HttpHeaders httpHeaders) {
        String plainCreds = format("%s:%s", jiraProps.getUsername(), jiraProps.getPassword());
        byte[] plainCredsBytes = plainCreds.getBytes(UTF_8);
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes, UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.add("X-Atlassian-Token", "no-check");
        if (mediaType != null) {
            headers.setContentType(mediaType);
        }
        if (httpHeaders != null) {
            headers.addAll(httpHeaders);
        }
        return headers;
    }

    public String getBaseUrl() {
        return jiraProps.getHost();
    }
}