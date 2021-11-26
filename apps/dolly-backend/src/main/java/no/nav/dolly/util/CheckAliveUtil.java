package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@UtilityClass
@Slf4j
public final class CheckAliveUtil {

    public static Map<String, String> checkConsumerAlive(NaisServerProperties serviceProperties, WebClient webClient, TokenExchange tokenService) {
        try {
            return Map.of(serviceProperties.getName(), serviceProperties.checkIsAlive(webClient, serviceProperties.getAccessToken(tokenService)));
        } catch (SecurityException | WebClientResponseException ex) {
            log.error("{} feilet mot URL: {}", serviceProperties.getName(), serviceProperties.getUrl(), ex);
            return Map.of(serviceProperties.getName(), String.format("%s, URL: %s", ex.getMessage(), serviceProperties.getUrl()));
        }
    }
}
