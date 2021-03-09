package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.synt.sykemelding.config.credentials.ArbeidsforholdApiServiceProperties;
import no.nav.registre.testnorge.synt.sykemelding.consumer.command.GetArbeidsforholdCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class ArbeidsforholdConsumer {
    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final ArbeidsforholdApiServiceProperties serviceProperties;

    public ArbeidsforholdConsumer(
            AccessTokenService accessTokenService,
            ArbeidsforholdApiServiceProperties serviceProperties
    ) {
        this.accessTokenService = accessTokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @SneakyThrows
    public ArbeidsforholdDTO getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        log.info("Henter arbeidsforhold for {} i org {} med id {}", ident, orgnummer, arbeidsforholdId);
        var token = accessTokenService.generateToken(serviceProperties).getTokenValue();

        ArbeidsforholdDTO arbeidsforholdDTO = new GetArbeidsforholdCommand(
                webClient,
                token,
                ident,
                orgnummer,
                arbeidsforholdId
        ).call();

        log.info("Arbeidsforhold for ident {} hentet.", arbeidsforholdDTO.getIdent());
        return arbeidsforholdDTO;
    }
}
