package no.nav.testnav.endringsmeldingservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.endringsmeldingservice.config.Consumers;
import no.nav.testnav.endringsmeldingservice.consumer.command.*;
import no.nav.testnav.endringsmeldingservice.domain.IdenterRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.IdentMiljoeDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Component
public class TpsMessagingConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange accessTokenService;

    public TpsMessagingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper,
            WebClient webClient
    ) {
        serverProperties = consumers.getTpsMessagingService();
        this.accessTokenService = tokenExchange;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<TpsIdentStatusDTO> hentMiljoer(IdenterRequest body) { // Skal ryddes til slutt
        return accessTokenService
                .exchange(serverProperties)
                .flatMapMany(accessToken -> new GetIdentEnvironmentsCommand(webClient, body.ident(), accessToken.getTokenValue()).call());
    }

    public Flux<IdentMiljoeDTO> getEksistererPerson(Set<String> identer, Set<String> miljoer) {

        return accessTokenService
                .exchange(serverProperties)
                .flatMapMany(accessToken -> new GetEksistererPersonCommand(webClient, identer, miljoer, accessToken.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Get eksisterende resultat {}", resultat));
    }

    public Flux<PersonMiljoeDTO> getPersondata(String ident, Set<String> miljoer) {

        return accessTokenService
                .exchange(serverProperties)
                .flatMapMany(accessToken -> new HentPersondataCommand(webClient, ident, miljoer, accessToken.getTokenValue()).call());
    }

    public Flux<AdressehistorikkDTO> getAdressehistorikk(String ident, LocalDate aksjonsdato, Set<String> miljoer) {

        return getAdressehistorikk(AdressehistorikkRequest.builder()
                        .ident(ident)
                        .aksjonsdato(aksjonsdato)
                        .build(),
                miljoer);
    }

    public Flux<AdressehistorikkDTO> getAdressehistorikk(AdressehistorikkRequest request, Set<String> miljoer) {

        return accessTokenService
                .exchange(serverProperties)
                .flatMapMany(accessToken ->
                        new GetAdressehistorikkCommand(webClient, request, miljoer, accessToken.getTokenValue()).call())
                .doOnNext(response -> log.info("Adressehistorikk mottatt: {}", response));
    }

    public Mono<DoedsmeldingResponse> sendKansellerDoedsmelding(PersonDTO person, Set<String> miljoer) {

        return accessTokenService
                .exchange(serverProperties)
                .flatMap(accessToken ->
                        new SendKansellerDoedsmeldingCommand(webClient, person, miljoer, accessToken.getTokenValue()).call())
                .doOnNext(response -> log.info("Mottatt status for kanseller dødsmelding {}", response));
    }

    public Mono<DoedsmeldingResponse> sendDoedsmelding(DoedsmeldingRequest request, Set<String> miljoer) {

        return accessTokenService
                .exchange(serverProperties)
                .flatMap(accessToken ->
                        new SendDoedsmeldingCommand(webClient, request, miljoer, accessToken.getTokenValue()).call())
                .doOnNext(response -> log.info("Mottatt status for dødsmelding {}", response));
    }

    public Mono<FoedselsmeldingResponse> sendFoedselsmelding(FoedselsmeldingRequest request, Set<String> miljoer) {

        log.info("Sender fødselsmelding til miljøer {} {}", miljoer, request);

        return accessTokenService
                .exchange(serverProperties)
                .flatMap(accessToken ->
                        new SendFoedselsmeldingCommand(webClient, request, miljoer, accessToken.getTokenValue()).call())
                .doOnNext(response -> log.info("Mottatt status for fødselsmelding {}", response));
    }
}
