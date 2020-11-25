package no.nav.registre.testnorge.organisasjonmottakservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.organisasjonmottakservice.consumer.command.GetCrumbCommand;
import no.nav.registre.testnorge.organisasjonmottakservice.consumer.command.StartBEREG007Command;
import no.nav.registre.testnorge.organisasjonmottakservice.consumer.request.JenkinsCrumb;
import no.nav.registre.testnorge.organisasjonmottakservice.domain.Flatfil;

@Slf4j
@Component
@DependencyOn(value = "jenkins", external = true)
public class JenkinsConsumer {
    private final Environment env;
    private final WebClient webClient;

    public JenkinsConsumer(
            Environment env,
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${jenkins.rest.api.url}") String jenkinsUri,
            @Value("${jenkins.username}") String jenkinsUsername,
            @Value("${jenkins.password}") String jenkinsPassword
    ) {
        WebClient.Builder builder = WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .defaultHeaders(headers -> headers.setBasicAuth(jenkinsUsername, jenkinsPassword))
                .baseUrl(jenkinsUri);

        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for Client Credentials", proxyHost);
            var uri = URI.create(proxyHost);

            HttpClient httpClient = HttpClient
                    .create()
                    .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(uri.getHost())
                            .port(uri.getPort())
                    ));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }

        this.webClient = builder.build();
        this.env = env;
    }

    public void send(Flatfil flatFile, String miljo) {
        var server = env.getProperty("JENKINS_SERVER_" + miljo.toUpperCase());
        if (server == null) {
            throw new RuntimeException("Finner ikke url for miljo: " + miljo);
        }
        JenkinsCrumb jenkinsCrumb = new GetCrumbCommand(webClient).call();
        new StartBEREG007Command(webClient, server, miljo, jenkinsCrumb, flatFile).run();
    }
}
