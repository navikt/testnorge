package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonmottak.config.Consumers;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.StartBEREG007Command;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.testnav.libs.commands.GetCrumbCommand;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

@Slf4j
@Component
public class JenkinsConsumer {

    private final JenkinsBatchStatusConsumer jenkinsBatchStatusConsumer;
    private final Environment env;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;

    public JenkinsConsumer(
            Environment env,
            Consumers consumers,
            TokenExchange tokenExchange,
            JenkinsBatchStatusConsumer jenkinsBatchStatusConsumer,
            OrganisasjonBestillingConsumer organisasjonBestillingConsumer,
            WebClient webClient
    ) {
        this.organisasjonBestillingConsumer = organisasjonBestillingConsumer;
        serverProperties = consumers.getTestnavDollyProxy();
        this.tokenExchange = tokenExchange;
        this.jenkinsBatchStatusConsumer = jenkinsBatchStatusConsumer;
        this.env = env;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl() + "/batch")
                .build();
    }

    public void send(Flatfil flatFile, String miljo, Set<String> uuids) {
        var accessToken = tokenExchange.exchange(serverProperties).block();

        var server = env.getProperty("JENKINS_SERVER_" + miljo.toUpperCase());
        if (server == null) {
            throw new IllegalArgumentException("Finner ikke url for miljo: " + miljo);
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
