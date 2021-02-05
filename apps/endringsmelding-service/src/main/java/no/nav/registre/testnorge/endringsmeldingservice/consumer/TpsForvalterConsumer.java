package no.nav.registre.testnorge.endringsmeldingservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import no.nav.registre.testnorge.endringsmeldingservice.config.credentias.TpsForvalterenProxyServiceProperties;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.command.SendDoedsmeldingCommand;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.command.SendFoedselsmeldingCommand;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.TpsDoedsmeldingDTO;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.TpsFoedselsmeldingDTO;
import no.nav.registre.testnorge.libs.dto.endringsmelding.v1.DoedsmeldingDTO;
import no.nav.registre.testnorge.libs.dto.endringsmelding.v1.FoedselsmeldingDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Component
public class TpsForvalterConsumer {
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;
    private final AccessTokenService accessTokenService;

    public TpsForvalterConsumer(TpsForvalterenProxyServiceProperties serverProperties, AccessTokenService accessTokenService) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public void sendFoedselsmelding(FoedselsmeldingDTO dto, Set<String> miljoer) {
        var accessToken = accessTokenService.generateToken(serverProperties);
        new SendFoedselsmeldingCommand(
                webClient,
                new TpsFoedselsmeldingDTO(dto, miljoer),
                accessToken.getTokenValue()
        ).run();
    }

    public void sendDoedsmelding(DoedsmeldingDTO dto, Set<String> miljoer) {
        var accessToken = accessTokenService.generateToken(serverProperties);
        new SendDoedsmeldingCommand(
                webClient,
                new TpsDoedsmeldingDTO(dto, miljoer),
                accessToken.getTokenValue()
        ).run();
    }
}
