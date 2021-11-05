package no.nav.dolly.consumer.jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.domain.jira.Project;
import no.nav.dolly.properties.JiraProps;
import no.nav.dolly.util.JacksonExchangeStrategyUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class JiraConsumer {

    private final JiraProps jiraProps;
    private final WebClient webClient;

    public JiraConsumer(JiraProps jiraProps, ObjectMapper objectMapper) {
        this.jiraProps = jiraProps;
        this.webClient = WebClient.builder()
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy(objectMapper))
                .baseUrl(jiraProps.getHost())
                .build();
    }

    public <T> ResponseEntity<T> excuteRequest(String url, HttpMethod httpMethod, HttpEntity httpEntity, Class<T> responseClass) {

        return httpMethod == HttpMethod.POST
                ? webClient
                .method(httpMethod)
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(url.split("/"))
                        .build())
                .bodyValue(httpEntity.getBody())
                .headers(httpHeaders -> httpHeaders.addAll(httpEntity.getHeaders()))
                .retrieve().toEntity(responseClass)
                .block()

                : webClient
                .method(httpMethod)
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(url.split("/"))
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(httpEntity.getHeaders()))
                .retrieve().toEntity(responseClass)
                .block();
    }

    public <T> ResponseEntity<Project> getOpenAmMetadata(String url, HttpEntity httpEntity, MultiValueMap<String, String> queries) {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(url.split("/"))
                        .queryParams(queries)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(httpEntity.getHeaders()))
                .retrieve().toEntity(Project.class)
                .block();
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