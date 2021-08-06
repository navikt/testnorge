package no.nav.registre.testnorge.generersyntameldingservice.consumer;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.registre.testnorge.generersyntameldingservice.config.credentials.SyntrestProperties;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostArbeidsforholdCommand;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostHistorikkCommand;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

@Slf4j
@Component
public class SyntrestConsumer {

    private final WebClient webClient;
    private final NaisServerProperties properties;
    private final AccessTokenService accessTokenService;

    public SyntrestConsumer(AccessTokenService accessTokenService, SyntrestProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public Arbeidsforhold getEnkeltArbeidsforhold(ArbeidsforholdPeriode periode, ArbeidsforholdType arbeidsforholdType) {
        var accessToken = accessTokenService.generateToken(properties).block();
        return new PostArbeidsforholdCommand(periode, webClient, arbeidsforholdType.getPath(), accessToken.getTokenValue()).call();
    }

    public List<Arbeidsforhold> getHistorikk(Arbeidsforhold arbeidsforhold) {
        var accessToken = accessTokenService.generateToken(properties).block();
        return new PostHistorikkCommand(webClient, arbeidsforhold, accessToken.getTokenValue()).call();
    }
}
