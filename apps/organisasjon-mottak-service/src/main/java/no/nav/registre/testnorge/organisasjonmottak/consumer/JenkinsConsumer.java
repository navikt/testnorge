package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.GetCrumbCommand;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.organisasjonmottak.config.properties.JenkinsServiceProperties;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.StartBEREG007Command;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;

@Slf4j
@Component
@DependencyOn(value = "jenkins", external = true)
public class JenkinsConsumer {
    private final JenkinsBatchStatusConsumer jenkinsBatchStatusConsumer;
    private final Environment env;
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public JenkinsConsumer(
            Environment env,
            JenkinsServiceProperties properties,
            AccessTokenService accessTokenService,
            JenkinsBatchStatusConsumer jenkinsBatchStatusConsumer
    ) {
        this.clientId = properties.getClientId();
        this.accessTokenService = accessTokenService;
        this.jenkinsBatchStatusConsumer = jenkinsBatchStatusConsumer;
        this.env = env;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();

    }

    public void send(Flatfil flatFile, String miljo, String uuid) {
        var accessToken = accessTokenService.generateClientCredentialAccessToken(clientId);

        var server = env.getProperty("JENKINS_SERVER_" + miljo.toUpperCase());
        if (server == null) {
            throw new RuntimeException("Finner ikke url for miljo: " + miljo);
        }
        JenkinsCrumb jenkinsCrumb = new GetCrumbCommand(webClient, accessToken.getTokenValue()).call();
        var id = new StartBEREG007Command(webClient, accessToken.getTokenValue(), server, miljo, jenkinsCrumb, flatFile).call();
        jenkinsBatchStatusConsumer.registerBestilling(uuid, miljo, id);
        log.info("Bestilling sendt til jenkins {}.", uuid);
    }
}
