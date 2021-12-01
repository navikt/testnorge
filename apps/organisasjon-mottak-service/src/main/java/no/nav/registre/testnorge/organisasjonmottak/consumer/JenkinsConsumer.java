package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import no.nav.registre.testnorge.organisasjonmottak.config.properties.JenkinsServiceProperties;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.StartBEREG007Command;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.testnav.libs.commands.GetCrumbCommand;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class JenkinsConsumer {
    private final JenkinsBatchStatusConsumer jenkinsBatchStatusConsumer;
    private final Environment env;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;
    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;

    public JenkinsConsumer(
            Environment env,
            JenkinsServiceProperties properties,
            TokenExchange tokenExchange,
            JenkinsBatchStatusConsumer jenkinsBatchStatusConsumer,
            OrganisasjonBestillingConsumer organisasjonBestillingConsumer
    ) {
        this.organisasjonBestillingConsumer = organisasjonBestillingConsumer;
        this.properties = properties;
        this.tokenExchange = tokenExchange;
        this.jenkinsBatchStatusConsumer = jenkinsBatchStatusConsumer;
        this.env = env;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();

    }

    public void send(Flatfil flatFile, String miljo, Set<String> uuids) {
        var accessToken = tokenExchange.generateToken(properties).block();

        var server = env.getProperty("JENKINS_SERVER_" + miljo.toUpperCase());
        if (server == null) {
            throw new RuntimeException("Finner ikke url for miljo: " + miljo);
        }
        JenkinsCrumb jenkinsCrumb = new GetCrumbCommand(webClient, accessToken.getTokenValue()).call();
        var id = new StartBEREG007Command(webClient, accessToken.getTokenValue(), server, miljo, jenkinsCrumb, flatFile).call();

        uuids.forEach(uuid -> {
            jenkinsBatchStatusConsumer.registerBestilling(uuid, miljo, id);
            log.info("Bestilling sendt til jenkins {}.", uuid);
            organisasjonBestillingConsumer.registerBestilling(uuid, miljo, id);
        });
    }
}
