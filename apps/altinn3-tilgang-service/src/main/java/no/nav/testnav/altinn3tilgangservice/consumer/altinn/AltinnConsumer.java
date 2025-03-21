package no.nav.testnav.altinn3tilgangservice.consumer.altinn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.altinn3tilgangservice.config.AltinnConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.CreateAccessListeMemberCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.DeleteAccessListMemberCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.GetAccessListMembersCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.GetAuthorizedPartiesCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.GetExchangeTokenCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AltinnAccessListResponseDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AltinnAuthorizedPartiesRequestDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AuthorizedPartyDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.BrregResponseDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonCreateDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonDeleteDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.brreg.BrregConsumer;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.MaskinportenConsumer;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonCreateDTO.ORGANISASJON_ID;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
public class AltinnConsumer {

    private final WebClient webClient;
    private final AltinnConfig altinnConfig;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;
    private final MaskinportenConsumer maskinportenConsumer;
    private final BrregConsumer brregConsumer;

    public AltinnConsumer(
            AltinnConfig altinnConfig,
            MaskinportenConsumer maskinportenConsumer,
            ObjectMapper objectMapper,
            MapperFacade mapperFacade,
            WebClient webClient,
            BrregConsumer brregConsumer
    ) {
        this.altinnConfig = altinnConfig;
        this.maskinportenConsumer = maskinportenConsumer;
        this.webClient = webClient
                .mutate()
                .baseUrl(altinnConfig.getUrl())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                })
                .build();
        this.brregConsumer = brregConsumer;
        this.mapperFacade = mapperFacade;
        this.objectMapper = objectMapper;
    }

    public Mono<String> exchangeToken(String token) {

        return new GetExchangeTokenCommand(webClient, token).call();
    }

    public Flux<Organisasjon> delete(String organisasjonsnummer) {

        return Flux.from(getAccessListMembers()
                        .flatMapMany(value -> Flux.fromIterable(value.getData()))
                        .map(AltinnAccessListResponseDTO.AccessListMembershipDTO::getIdentifiers)
                        .collectList()
                        .map(data -> getIdentifier(data, organisasjonsnummer))
                        .map(identifier ->
                                !identifier.getData().isEmpty() ?
                                        maskinportenConsumer.getAccessToken()
                                                .flatMap(this::exchangeToken)
                                                .flatMap(exchangeToken -> new DeleteAccessListMemberCommand(
                                                        webClient,
                                                        exchangeToken,
                                                        identifier,
                                                        altinnConfig).call())
                                                .flatMapMany(this::convertToOrganisasjon) :
                                        Flux.just(Organisasjon.builder()
                                                .organisasjonsnummer(organisasjonsnummer)
                                                .feilmelding("404 Not found: Organisasjon %s ble ikke funnet".formatted(organisasjonsnummer))
                                                .build())))
                .flatMap(Flux::from);
    }

    public Mono<Organisasjon> create(String organisasjonsnummer) {

        return Mono.from(maskinportenConsumer.getAccessToken()
                .flatMap(this::exchangeToken)
                .flatMap(exchangeToken -> new CreateAccessListeMemberCommand(
                        webClient,
                        exchangeToken,
                        new OrganisasjonCreateDTO(organisasjonsnummer),
                        altinnConfig).call())
                .flatMapMany(response ->
                        isBlank(response.getFeilmelding()) ?
                                Flux.fromIterable(response.getData())
                                        .map(this::getOrgnummer)
                                        .filter(organisasjonsnummer::equals)
                                        .flatMap(brregConsumer::getEnhet) :
                                Mono.just(BrregResponseDTO.builder()
                                        .organisasjonsnummer(organisasjonsnummer)
                                        .feilmelding(response.getFeilmelding())
                                        .status(response.getStatus())
                                        .build()))
                .map(response -> mapperFacade.map(response, Organisasjon.class)));
    }

    public Flux<Organisasjon> getOrganisasjoner() {

        return getAccessListMembers()
                .flatMapMany(this::convertToOrganisasjon);
    }

    public Flux<AuthorizedPartyDTO> getAuthorizedParties(String ident) {

        return maskinportenConsumer.getAccessToken()
                .flatMap(this::exchangeToken)
                .flatMap(exchangeToken -> new GetAuthorizedPartiesCommand(webClient,
                        new AltinnAuthorizedPartiesRequestDTO(ident),
                        exchangeToken).call())
                .map(Arrays::asList)
                .flatMapIterable(list -> list);
    }

    private Mono<AltinnAccessListResponseDTO> getAccessListMembers() {

        return maskinportenConsumer.getAccessToken()
                .flatMap(this::exchangeToken)
                .flatMap(exchangeToken -> new GetAccessListMembersCommand(
                        webClient,
                        exchangeToken,
                        altinnConfig).call());
    }

    private Flux<Organisasjon> convertToOrganisasjon(AltinnAccessListResponseDTO altInnResponse) {

        return Flux.fromIterable(altInnResponse.getData())
                .map(this::getOrgnummer)
                .flatMap(brregConsumer::getEnhet)
                .map(response -> mapperFacade.map(response, Organisasjon.class));
    }

    private OrganisasjonDeleteDTO getIdentifier(List<JsonNode> data, String organisasjonsnummer) {

        return data.stream()
                .filter(identifier -> organisasjonsnummer.equals(identifier.get(ORGANISASJON_ID).asText()))
                .map(identifier -> objectMapper.convertValue(identifier,
                        new TypeReference<Map<String, String>>() {
                        }))
                .map(OrganisasjonDeleteDTO::new)
                .findFirst().orElse(new OrganisasjonDeleteDTO());
    }

    @SneakyThrows
    private String getOrgnummer(AltinnAccessListResponseDTO.AccessListMembershipDTO data) {

        return data.getIdentifiers()
                .get(ORGANISASJON_ID)
                .asText();
    }
}