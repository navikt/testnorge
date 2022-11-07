package no.nav.dolly.bestilling.personservice;

import no.nav.dolly.bestilling.personservice.command.PersonServiceExistCommand;
import no.nav.dolly.config.credentials.PersonServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class PersonServiceConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public PersonServiceConsumer(TokenExchange tokenService,
                                 PersonServiceProperties serverProperties,
                                 ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "personService_isPerson"})
    public Boolean isPerson(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new PersonServiceExistCommand(webClient, ident, token.getTokenValue()).call())
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    public Map<String, Object> checkStatus() {
        var statusMap =  CheckAliveUtil.checkConsumerStatus(
                serviceProperties.getUrl() + "/internal/isAlive",
                serviceProperties.getUrl() + "/internal/isReady",
                WebClient.builder().build());
        statusMap.put("team", "Dolly");
        statusMap.put("alive-url", serviceProperties.getUrl() + "/internal/isAlive");

        var registerStatus = CheckAliveUtil.checkConsumerStatus(
                "https://testnav-person-service.dev.intern.nav.no/internal/isAlive",
                "https://testnav-person-service.dev.intern.nav.no/internal/isReady",
                WebClient.builder().build());
        registerStatus.put("team", "Dolly");
        registerStatus.put("alive-url", "https://testnav-person-service.dev.intern.nav.no/internal/isAlive");

//        var registerStatus = CheckAliveUtil.checkConsumerStatus(
//                "https://testnav-person-service.dev.intern.nav.no/internal/isAlive",
//                "https://testnav-person-service.dev.intern.nav.no/internal/isReady",
//                WebClient.builder().build());
//        registerStatus.put("team", "Dolly");

        return Map.of(
                "PersonService-proxy", statusMap,
                "PersonService", registerStatus
        );
    }
}
