package no.nav.testnav.altinn3tilgangservice.consumer.altinn;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.altinn3tilgangservice.config.AltinnConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.CreateAccessListeMemberCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.DeleteAccessListMemberCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.GetAccessListMembersCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.GetExchangeTokenCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AltinnResponseDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.DeleteStatus;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonCreateDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.brreg.BrregConsumer;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.MaskinportenConsumer;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AltinnConsumer {

    private final WebClient webClient;
    private final AltinnConfig altinnConfig;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;
    private final MaskinportenConsumer maskinportenConsumer;
    private final BrregConsumer brregConsumer;

    public AltinnConsumer(
            AltinnConfig altinnConfig,
            MaskinportenConsumer maskinportenConsumer,
            ObjectMapper objectMapper,
            MapperFacade mapperFacade,
            WebClient.Builder webClientBuilder, BrregConsumer brregConsumer) {

        this.altinnConfig = altinnConfig;
        this.maskinportenConsumer = maskinportenConsumer;
        this.webClient = webClientBuilder
                .baseUrl(altinnConfig.getUrl())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                })
                .filters(exchangeFilterFunctions ->
                        exchangeFilterFunctions.add(logRequest()))
                .build();
        this.brregConsumer = brregConsumer;
        this.objectMapper = objectMapper;
        this.mapperFacade = mapperFacade;
    }

    private ExchangeFilterFunction logRequest() {

        return (clientRequest, next) -> {
            var buffer = new StringBuilder(250)
                    .append("Request: ")
                    .append(clientRequest.method())
                    .append(' ')
                    .append(clientRequest.url())
                    .append(System.lineSeparator());

            clientRequest.headers()
                    .forEach((name, values) -> values
                            .forEach(value -> buffer.append('\t')
                                    .append(name)
                                    .append('=')
                                    .append(value.contains("Bearer ") ? "Bearer token" : value)
                                    .append(System.lineSeparator())));
            log.trace(buffer.substring(0, buffer.length() - 1));
            return next.exchange(clientRequest);
        };
    }

    public Mono<String> exchangeToken(String token) {

        return new GetExchangeTokenCommand(webClient, token).call()
                .doOnNext(response -> log.info("Exchange token {}", response));
    }


    public Flux<DeleteStatus> delete(String organiasjonsnummer) {

        return getAccessListMembers()
                .flatMapMany(value -> Flux.fromIterable(value.getData()))
                .map(AltinnResponseDTO.AccessListMembershipDTO::getIdentifiers)
                .filter(identifier -> identifier.contains(organiasjonsnummer))
                .flatMap(identifier -> maskinportenConsumer.getAccessToken()
                        .flatMap(this::exchangeToken)
                        .flatMap(exchangeToken -> new DeleteAccessListMemberCommand(
                                webClient,
                                exchangeToken,
                                identifier,
                                altinnConfig).call())
                );
    }

    public Mono<Organisasjon> create(String organisasjonsnummer) {

        return maskinportenConsumer.getAccessToken()
                .flatMap(this::exchangeToken)
                .flatMap(exchangeToken -> new CreateAccessListeMemberCommand(
                        webClient,
                        exchangeToken,
                        new OrganisasjonCreateDTO(organisasjonsnummer),
                        altinnConfig).call())
                .map(altinnResponse -> altinnResponse.getData().getFirst())
                .map(AltinnResponseDTO.AccessListMembershipDTO::getIdentifiers)
                .map(this::getOrgnummer)
                .flatMap(brregConsumer::getEnheter)
                .map(response -> mapperFacade.map(response, Organisasjon.class));
    }

    public Flux<Organisasjon> getOrganisasjoner() {

        return getAccessListMembers()
                .flatMapMany(accessList -> Flux.fromIterable(accessList.getData())
                        .map(AltinnResponseDTO.AccessListMembershipDTO::getIdentifiers)
                        .map(this::getOrgnummer)
                        .flatMap(brregConsumer::getEnheter)
                        .map(response -> mapperFacade.map(response, Organisasjon.class)));
    }

    private Mono<AltinnResponseDTO> getAccessListMembers() {

        return maskinportenConsumer.getAccessToken()
                .flatMap(this::exchangeToken)
                .flatMap(exchangeToken -> new GetAccessListMembersCommand(
                                webClient,
                                exchangeToken,
                                altinnConfig
                        ).call()
                        .doOnNext(response -> log.info("Hentet organisasjontilgang for {}", response))
                );
    }

    @SneakyThrows
    private String getOrgnummer(String identifiers) {

        return objectMapper.readTree(identifiers)
                .path("urn:altinn:organization:identifier-no")
                .asText();
    }
}