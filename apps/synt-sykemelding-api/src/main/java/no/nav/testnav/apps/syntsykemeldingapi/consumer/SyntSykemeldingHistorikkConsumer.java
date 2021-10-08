package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import java.time.LocalDate;
import java.util.Map;

import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.PostSyntSykemeldingCommand;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.SyntSykemeldingProperties;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.testnav.apps.syntsykemeldingapi.exception.GenererSykemeldingerException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SyntSykemeldingHistorikkConsumer {

    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntSykemeldingHistorikkConsumer(
            SyntSykemeldingProperties syntProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = syntProperties;
        this.tokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntProperties.getUrl())
                .build();
    }

    @SneakyThrows
    public SyntSykemeldingHistorikkDTO genererSykemeldinger(String ident, LocalDate startDato) {
        log.info("Generererer sykemelding for {} fom {}", ident, startDato.toString());

        try {
            var request = Map.of(ident, startDato);
            var accessToken = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();

            var response = new PostSyntSykemeldingCommand(request, accessToken, webClient).call();

            if (response == null) {
                throw new GenererSykemeldingerException("Klarte ikke å generere sykemeldinger. Response objectet er null.");
            }
            log.info("Sykemelding generert.");
            return response.get(ident);
        } catch (Exception e) {
            throw new GenererSykemeldingerException("Klarte ikke å generere sykemeldinger.");
        }
    }
}