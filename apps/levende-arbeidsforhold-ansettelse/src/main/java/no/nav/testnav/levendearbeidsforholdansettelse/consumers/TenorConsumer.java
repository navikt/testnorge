package no.nav.testnav.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.tenor.HentOrganisasjonCommand;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.OrganisasjonResponseDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Collection;

import static java.util.Objects.nonNull;
import static no.nav.testnav.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonSelectOptions.OrganisasjonForm.BEDR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class TenorConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public TenorConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient
    ) {
        this.serverProperties = consumers.getTestnavTenorSearchService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<OrganisasjonResponseDTO> hentOrganisasjon() {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new HentOrganisasjonCommand(webClient, token.getTokenValue(),
                        TenorOrganisasjonRequest.builder()
                                .organisasjonsform(TenorOrganisasjonRequest.Organisasjonsform.builder()
                                        .kode(BEDR)
                                        .build())
                                .build())
                        .call())
                .doOnNext(resultat -> log.info("Hentet fra Tenorsøk: {} ", resultat))
                .map(resultat -> OrganisasjonResponseDTO.builder()
                        .orgnummer(resultat.getData().getOrganisasjoner().stream()
                                .map(OrganisasjonDTO.Organisasjon::getOrganisasjonsnummer)
                                .findFirst().orElse(null))
                        .postnummer(resultat.getData().getOrganisasjoner().stream()
                                .map(OrganisasjonDTO.Organisasjon::getTenorRelasjoner)
                                .map(OrganisasjonDTO.TenorRelasjoner::getBrregErFr)
                                .flatMap(Collection::stream)
                                .map(TenorConsumer::getPostnummer)
                                .findFirst().orElse(null))
                        .build());
    }

    private static String getPostnummer(OrganisasjonDTO.BrregTrivia brregTrivia) {

        String postnummer;

        if (nonNull(brregTrivia.getForretningsadresse())) {
            postnummer = brregTrivia.getForretningsadresse().getPostnummer();

        } else if (nonNull(brregTrivia.getPostadresse())) {
            postnummer = brregTrivia.getPostadresse().getPostnummer();

        } else {
            postnummer = null;
        }

        return isNotBlank(postnummer) ? postnummer : "0000";
    }
}
