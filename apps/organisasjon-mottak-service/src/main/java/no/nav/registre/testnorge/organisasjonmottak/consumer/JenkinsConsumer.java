package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.GetCrumbCommand;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.StartBEREG007Command;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;

@Slf4j
@Component
@DependencyOn(value = "jenkins", external = true)
public class JenkinsConsumer {
    private final JenkinsBatchStatusConsumer jenkinsBatchStatusConsumer;
    private final Environment env;
    private final WebClient webClient;

    public JenkinsConsumer(
            Environment env,
            @Value("${jenkins.rest.api.url}") String jenkinsUri,
            JenkinsBatchStatusConsumer jenkinsBatchStatusConsumer
    ) {
        this.jenkinsBatchStatusConsumer = jenkinsBatchStatusConsumer;
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(jenkinsUri);

        this.webClient = builder.build();
        this.env = env;
    }

    public void send(Flatfil flatFile, String miljo, String uuid) {
        var server = env.getProperty("JENKINS_SERVER_" + miljo.toUpperCase());
        if (server == null) {
            throw new RuntimeException("Finner ikke url for miljo: " + miljo);
        }
        JenkinsCrumb jenkinsCrumb = new GetCrumbCommand(webClient, "dummy").call();
        var id = new StartBEREG007Command(webClient, server, miljo, jenkinsCrumb, flatFile).call();
        jenkinsBatchStatusConsumer.registerBestilling(uuid, miljo, id);
        log.info("Bestilling sendt til jenkins {}.", uuid);
    }
}
