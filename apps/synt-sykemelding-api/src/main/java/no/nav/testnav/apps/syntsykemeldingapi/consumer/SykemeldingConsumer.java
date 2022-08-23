package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.TestnorgeSykemeldingProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.PostSykemeldingCommand;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SykemeldingConsumer {
    private final WebClient webClient;

    public SykemeldingConsumer(
            TestnorgeSykemeldingProperties serviceProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public void opprettSykemelding(Sykemelding sykemelding) {
        new PostSykemeldingCommand(webClient, sykemelding).call().block();
    }
}
