package no.nav.registre.testnorge.levendearbeidsforhold.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.servletsecurity.exchange.ExchangeToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.vault.authentication.JwtAuthentication;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for å sjekke arbeidsforhold.
 * Henter arbeidsforhold fra Aareg og logger informasjonen.
 * @see no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {
    private final HentArbeidsforholdConsumer hentArbeidsforholdConsumer;
    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;
    private final Consumers consumers;
    private String issuerUri;
    private String jwkSetUri;
    private String id;
    private List<String> acceptedAudience;
    @EventListener(ApplicationReadyEvent.class)
    public void sjekkArbeidsforhold() {
        WebClient webClient = WebClient.create();
        ResourceServerType resourceServerType = getAuthenticatedResourceServerType.call();
        ServerProperties serverProperties = ServerProperties.of("dev-gcp", "dolly", "aareg", "https://aareg-services-q2.intern.dev.nav.no");

        TokenExchange tokenExchange = new TokenExchange(getAuthenticatedResourceServerType, new ArrayList<>(), new ObjectMapper());

        HentArbeidsforholdConsumer arbeid = new HentArbeidsforholdConsumer(consumers, tokenExchange, new ObjectMapper());
        id = "30447515845";
        List<ArbeidsforholdDTO> arbeidsforhold = hentArbeidsforholdConsumer.getArbeidsforhold(id);
        if (arbeidsforhold != null) {
            log.info("Arbeidsforhold funnet: {}", arbeidsforhold);
        } else {
            log.warn("Fant ikke arbeidsforhold med id: {}", id);
        }
    }

}
//Les i appen ArbeidsforholdService