package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.ArbeidsforholdServiceProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.GetArbeidsforholdCommand;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class ArbeidsforholdConsumer {
    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final ArbeidsforholdServiceProperties serviceProperties;

    public ArbeidsforholdConsumer(
            AccessTokenService accessTokenService,
            ArbeidsforholdServiceProperties serviceProperties
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
        var token = accessTokenService.generateToken(serviceProperties).block().getTokenValue();

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
