package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.MatrikkeladresseServiceCommand;
import no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand;
import no.nav.testnav.libs.data.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class AdresseServiceConsumer {

    private static final String UOPPGITT = "9999";
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public AdresseServiceConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getAdresseService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO getVegadresse(VegadresseDTO vegadresse, String matrikkelId) {

        var startTime = currentTimeMillis();

        if (UOPPGITT.equals(vegadresse.getKommunenummer())) {
            var adresser = tokenExchange.exchange(serverProperties)
                    .flatMap(token -> new VegadresseServiceCommand(webClient, new VegadresseDTO(), null, token.getTokenValue()).call())
                    .block();

            return no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                    .postnummer(Arrays.stream(adresser)
                            .findFirst()
                            .orElse(no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                                    .postnummer("9999")
                                    .build())
                            .getPostnummer())
                    .build();
        }

        var adresser = tokenExchange.exchange(serverProperties).flatMap(
                        token -> new VegadresseServiceCommand(webClient, vegadresse, matrikkelId, token.getTokenValue()).call())
                .block();

        log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime);
        return Stream.of(adresser).findFirst()
                .orElse(VegadresseServiceCommand.defaultAdresse());
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO getMatrikkeladresse(MatrikkeladresseDTO adresse, String matrikkelId) {

        var startTime = currentTimeMillis();

        var adresser = tokenExchange.exchange(serverProperties).flatMap(
                        token -> new MatrikkeladresseServiceCommand(webClient, adresse, matrikkelId, token.getTokenValue()).call())
                .block();

        log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime);
        return Stream.of(adresser).findFirst()
                .orElse(MatrikkeladresseServiceCommand.defaultAdresse());
    }
}
