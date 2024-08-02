package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.kodeverk.KodeverkServiceCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
//import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
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

    public List<String> hentKodeverkListe(String kodeverk){
        var accessToken = tokenExchange.exchange(serverProperties).block();
        Map<String, String> koder = new KodeverkServiceCommand(webClient, accessToken.getTokenValue(), kodeverk).call();
        return new ArrayList<>(koder.keySet());
    }


}
