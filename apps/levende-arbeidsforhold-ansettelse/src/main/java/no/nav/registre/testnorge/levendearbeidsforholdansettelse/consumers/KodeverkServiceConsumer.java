package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.KodeverkServiceCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.KodeverkBetydningerResponse;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.KodeverkResponse;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class KodeverkServiceConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public KodeverkServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {
        serverProperties = consumers.getTestnavKodeverkService();
        log.info("ServerProperties: {}", serverProperties);
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(exchangeStrategies)
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Map<String, String> hentKodeverk(String kodeverk) {
        var accessToken = tokenExchange.exchange(serverProperties).block();
        return new KodeverkServiceCommand(webClient, accessToken.getTokenValue(), kodeverk).call();

    }
}
