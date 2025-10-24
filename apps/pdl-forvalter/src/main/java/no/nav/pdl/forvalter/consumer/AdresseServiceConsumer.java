package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.MatrikkeladresseServiceCommand;
import no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand;
import no.nav.testnav.libs.data.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.remove;

@Slf4j
@Service
public class AdresseServiceConsumer {

    private static final String UOPPGITT = "9999";
    private static final String HISTORISK = "(historisk)";

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final MapperFacade mapperFacade;
    private final KodeverkConsumer kodeverkConsumer;

    public AdresseServiceConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient,
            MapperFacade mapperFacade,
            KodeverkConsumer kodeverkConsumer) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getAdresseService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.mapperFacade = mapperFacade;
        this.kodeverkConsumer = kodeverkConsumer;
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO getVegadresse(VegadresseDTO vegadresse, String matrikkelId) {

        var startTime = currentTimeMillis();
        var vegadresseDTO = mapperFacade.map(vegadresse, VegadresseDTO.class);

        if (vegadresse.getKommunenummer().equals(UOPPGITT)) {
            vegadresseDTO.setKommunenummer(null);
        } else {
            vegadresseDTO.setKommunenummer(sjekkHistorisk(vegadresse));
        }

        if (UOPPGITT.equals(vegadresseDTO.getKommunenummer())) {
            vegadresseDTO.setKommunenummer(null);
        }

        return tokenExchange.exchange(serverProperties)
                .flatMap(token ->
                        new VegadresseServiceCommand(webClient, vegadresseDTO, matrikkelId, token.getTokenValue()).call())
                .flatMapMany(Flux::fromArray)
                .next()
                .switchIfEmpty(Mono.defer(() -> Mono.just(VegadresseServiceCommand.defaultAdresse())))
                .doOnNext(adresse -> log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime))
                .map(adresse -> {
                    adresse.setKommunenummer(vegadresse.getKommunenummer());
                    return adresse;
                })
                .block();
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO getMatrikkeladresse(MatrikkeladresseDTO adresse, String matrikkelId) {

        var startTime = currentTimeMillis();

        return tokenExchange.exchange(serverProperties)
                .flatMap(token ->
                        new MatrikkeladresseServiceCommand(webClient, adresse, matrikkelId, token.getTokenValue()).call())
                .flatMapMany(Flux::fromArray)
                .next()
                .switchIfEmpty(Mono.defer(() -> Mono.just(MatrikkeladresseServiceCommand.defaultAdresse())))
                .doOnNext(adresseDTO -> log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime))
                .block();
    }

    private String sjekkHistorisk(VegadresseDTO vegadresse) {

        if (isNotBlank(vegadresse.getKommunenummer())) {
            var historiske = kodeverkConsumer.getKommunerMedHistoriske();
            var kommunenavn = historiske.get(vegadresse.getKommunenummer());
            var gjeldendeKommunenavn = remove(kommunenavn, HISTORISK).trim();
            return historiske.entrySet().stream()
                    .filter(kommune -> kommune.getValue().equals(gjeldendeKommunenavn))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }
}
