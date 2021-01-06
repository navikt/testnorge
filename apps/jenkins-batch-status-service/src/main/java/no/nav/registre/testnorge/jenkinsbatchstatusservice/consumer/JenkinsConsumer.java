package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetBEREG007LogCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetQueueItemCommand;
import no.nav.registre.testnorge.libs.common.command.GetCrumbCommand;
import no.nav.registre.testnorge.libs.dto.jenkins.v1.JenkinsCrumb;

@Component
public class JenkinsConsumer {
    private final WebClient webClient;

    public JenkinsConsumer(@Value("${consumers.jenkins.url}") String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    private JenkinsCrumb getCrumb() {
        return new GetCrumbCommand(webClient).call();
    }

    public Long getJobNumber(Long itemId) {
        var dto = new GetQueueItemCommand(webClient, getCrumb(), itemId).call();
        return dto.getNumber();
    }

    public String getJobLog(Long jobNumber) {
        return new GetBEREG007LogCommand(webClient, jobNumber).call();
    }
}
