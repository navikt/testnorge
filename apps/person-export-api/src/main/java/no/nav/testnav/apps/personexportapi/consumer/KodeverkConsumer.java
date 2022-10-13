package no.nav.testnav.apps.personexportapi.consumer;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.testnav.apps.personexportapi.consumer.command.GetKodeverkCommand;
import no.nav.testnav.apps.personexportapi.consumer.credential.KodeverkProperties;
import no.nav.testnav.apps.personexportapi.consumer.response.KodeverkBetydningerResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@EnableCaching
@CacheConfig(cacheNames = "Kodeverk")
public class KodeverkConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public KodeverkConsumer(
            TokenExchange tokenExchange,
            KodeverkProperties serviceProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.properties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public String getKodeverkOppslag(String kodeverk, String verdi) {
        try {
            return isNotBlank(verdi) ? getKodeverkByName(kodeverk).get(verdi).stream().findFirst()
                    .map(betydning -> betydning.getBeskrivelser().get("nb").getTekst())
                    .orElse(null) : null;

        } catch (RuntimeException e) {
            return null;
        }
    }

    @Cacheable(sync = true)
    public Map<String, List<KodeverkBetydningerResponse.Betydning>> getKodeverkByName(String kodeverk) {

        var kodeverkResponse = tokenExchange.exchange(properties)
                .flatMap(accessToken -> new GetKodeverkCommand(webClient, accessToken.getTokenValue(), kodeverk).call())
                .block();

        return kodeverkResponse != null ? kodeverkResponse.getBetydninger() : Collections.emptyMap();
    }
}
