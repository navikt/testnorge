package no.nav.testnav.endringsmeldingservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import no.nav.testnav.endringsmeldingservice.config.credentias.TpsForvalterenProxyServiceProperties;
import no.nav.testnav.endringsmeldingservice.consumer.command.GetIdentEnvironmentsCommand;
import no.nav.testnav.endringsmeldingservice.consumer.command.SendDoedsmeldingCommand;
import no.nav.testnav.endringsmeldingservice.consumer.command.SendFoedselsmeldingCommand;
import no.nav.testnav.endringsmeldingservice.consumer.dto.TpsDoedsmeldingDTO;
import no.nav.testnav.endringsmeldingservice.consumer.request.TpsFoedselsmeldingRequest;
import no.nav.testnav.endringsmeldingservice.domain.Status;
import no.nav.testnav.libs.dto.endringsmelding.v1.DoedsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v1.FoedselsmeldingDTO;
import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

@Component
public class TpsForvalterConsumer {
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;
    private final AccessTokenService accessTokenService;

    public TpsForvalterConsumer(
            TpsForvalterenProxyServiceProperties serverProperties,
            AccessTokenService accessTokenService,
            ObjectMapper objectMapper
    ) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;

        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();


        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Set<String> hentMiljoer(String ident) {
        var accessToken = accessTokenService.generateToken(serverProperties).block();
        return new GetIdentEnvironmentsCommand(webClient, ident, accessToken.getTokenValue()).call();
    }

    public Status sendFoedselsmelding(FoedselsmeldingDTO dto, Set<String> miljoer) {
        var accessToken = accessTokenService.generateToken(serverProperties).block();
        var response = new SendFoedselsmeldingCommand(
                webClient,
                new TpsFoedselsmeldingRequest(dto, miljoer),
                accessToken.getTokenValue()
        ).call();
        return new Status(response);
    }

    public Status sendDoedsmelding(DoedsmeldingDTO dto, Set<String> miljoer) {
        var accessToken = accessTokenService.generateToken(serverProperties).block();
        var response = new SendDoedsmeldingCommand(
                webClient,
                new TpsDoedsmeldingDTO(dto, miljoer),
                accessToken.getTokenValue()
        ).call();
        return new Status(response);
    }
}
