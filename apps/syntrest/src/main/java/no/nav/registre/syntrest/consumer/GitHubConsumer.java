package no.nav.registre.syntrest.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Slf4j
@Component
public class GitHubConsumer {
    /**
     * The GitHub Consumer provides an interface to GitHub, primarily to retrieve package tags.
     */

    private final WebClient.Builder webClientBuilder;
    private final URL url;
    private final String username;
    private final String password;

    public GitHubConsumer(
            @Value("${github_username}") String username,
            @Value("${github_password}") String password,
            @Value("${github_url}") String githubUrl,
            @Value("${proxy-url") String proxyUrl,
            @Value("${proxy-port}") int proxyPort,
            WebClient.Builder webClientBuilder
    ) throws MalformedURLException {
        this.url = new URL(githubUrl);
        this.webClientBuilder = webClientBuilder.baseUrl(githubUrl);

        if (!"local".equals(proxyUrl) && proxyPort != 0) {
            HttpClient httpClient = HttpClient.create()
                    .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(proxyUrl)
                            .port(proxyPort)));

            ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
            this.webClientBuilder.clientConnector(connector);
        }

        this.username = username;
        this.password = password;
    }

    public String getApplicationTag(String appName) {
        String packageName = getCorrectGithubPackageName(appName);
        String queryString = "query {repository(owner:\"navikt\", name:\"testnorge-syntetiseringspakker\") {packages(names:[\"" +
                packageName + "\"] last:1) {nodes {latestVersion{version}} }}}";

        JsonNode queryAnswer = webClientBuilder.build()
                .post()
                .uri(this.url.getPath())
                .headers(headers -> {
                    headers.setBasicAuth(username, password);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(Mono.just(new GraphQlQuery(queryString)), GraphQlQuery.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (!Objects.isNull(queryAnswer)) {
            String tag = queryAnswer.findValue("version").asText();
            log.info("Found {} version {}.", appName, tag);
            return tag;
        }

        log.warn("Could not find tag for application {}.", appName);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find application tag.");
    }

    private String getCorrectGithubPackageName(String appName) {
        int bindestrekIndex = appName.lastIndexOf("-");
        String app = appName.substring(bindestrekIndex + 1);
        return "synt_" + app;
    }

    private static final class GraphQlQuery {
        private final String query;

        public GraphQlQuery(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }
}
