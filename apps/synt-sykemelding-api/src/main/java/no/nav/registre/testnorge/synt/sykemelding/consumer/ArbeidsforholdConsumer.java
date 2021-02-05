package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.synt.sykemelding.consumer.command.GetArbeidsforholdCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
@DependencyOn("testnorge-arbeidsforhold-api")
public class ArbeidsforholdConsumer {
    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final String clientId;

    public ArbeidsforholdConsumer(
            AccessTokenService accessTokenService,
            @Value("${consumers.arbeidsforhold.client_id}") String clientId,
            @Value("${consumers.arbeidsforhold.url}") String url
    ) {
        this.accessTokenService = accessTokenService;
        this.clientId = clientId;
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    @SneakyThrows
    public ArbeidsforholdDTO getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {

        log.info("Henter arbeidsforhold for {} i org {} med id {}", ident, orgnummer, arbeidsforholdId);

        ArbeidsforholdDTO arbeidsforholdDTO = new GetArbeidsforholdCommand(
                webClient,
                accessTokenService.generateToken(new AccessScopes("api://" + clientId + "/.default")).getTokenValue(),
                ident,
                orgnummer,
                arbeidsforholdId
        ).call();

        log.info("Arbeidsforhold for ident: {} hentet", arbeidsforholdDTO.getIdent());
        return arbeidsforholdDTO;
    }
}
