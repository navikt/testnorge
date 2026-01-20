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

import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;
import static no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonCreateDTO.ORGANISASJON_ID;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class AltinnConsumer {

    private static final int MAX_PAGES = 100;

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
                        .flatMapMany(Flux::fromIterable)
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
                                                .map(AltinnAccessListResponseDTO::getData)
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

    private Mono<List<AltinnAccessListResponseDTO.AccessListMembershipDTO>> getAccessListMembers() {

        var token = "eyJraWQiOiJiZFhMRVduRGpMSGpwRThPZnl5TUp4UlJLbVo3MUxCOHUxeUREbVBpdVQwIiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZSI6ImFsdGlubjphY2Nlc3NtYW5hZ2VtZW50L2F1dGhvcml6ZWRwYXJ0aWVzLnJlc291cmNlb3duZXIgYWx0aW5uOnJlc291cmNlcmVnaXN0cnkvYWNjZXNzbGlzdC53cml0ZSBhbHRpbm46cmVzb3VyY2VyZWdpc3RyeS9hY2Nlc3NsaXN0LnJlYWQiLCJpc3MiOiJodHRwczovL3Rlc3QubWFza2lucG9ydGVuLm5vLyIsImNsaWVudF9hbXIiOiJwcml2YXRlX2tleV9qd3QiLCJ0b2tlbl90eXBlIjoiQmVhcmVyIiwiZXhwIjoxNzY4ODk0MjIyLCJpYXQiOjE3Njg4OTA2MjIsImNsaWVudF9pZCI6ImVmMjk2MGRlLTdmYTYtNDM5Ni04MGE1LTJlY2EwMGU0YWYyOCIsImp0aSI6IkxzUFhvMHRjMFFqaXZZaEI1ajRpVW1ZaFBJbmQ4MC1XdmRxSXk0U05wNW8iLCJjb25zdW1lciI6eyJhdXRob3JpdHkiOiJpc282NTIzLWFjdG9yaWQtdXBpcyIsIklEIjoiMDE5Mjo4ODk2NDA3ODIifX0.Kkv4vQW3uRZRDB8OXOjbH9BD_rxyLEa8d4pW6xX9StfkxCKTGQ032m9yW8uMRA8MB2yFllxcpSTPhv5OQZzTscDuo1qbtvSNAEmdBkiZ3EegoAiQNDTUwGzYS64YfH6V2Yw-1xAsN9x_QKZ1RwN-cGnTIj0AXelDpbNZgW5ARdVLGokMJghFBY9nr9hAnYaNdH-l1CfsxzGTXRL5d4o9JNEfL_sG65GxGfSAtK_5dGAySaB4SlUkBe2PsNBXN_SCw-DMD39237_Mr2i9jyCdnoU8qFMDKLNPFm4Q7fMGKvY8UpGGKdCWbIKn82QzPCU6KZ17ORZkzCCHAALXV9eULqW7YkXFEyC0zKoD618l-l5uJJpp4IlHxWVBtrZIoA4epoonu-7aL13LG6UB4sfsOf2chsLMQjNPEx2Uehp61Z6lK2O6jPuDROakTwkmbdyDK6D-55vkzNwr52-GEcMk9fDGxpc6MKtDKeGMYXsbfEV9rkNgkOW18Fb7xHx_FinP";

        return exchangeToken(token)
//        return maskinportenConsumer.getAccessToken()
//                .flatMap(this::exchangeToken)
                .flatMap(this::getAccessListMembers);
    }

    private Mono<List<AltinnAccessListResponseDTO.AccessListMembershipDTO>> getAccessListMembers(String exchangeToken) {

        var counter = new AtomicInteger(0);
        return new GetAccessListMembersCommand(
                webClient,
                Optional.empty(),
                exchangeToken,
                altinnConfig).call()
                .expand(response -> {

                    if (nonNull(response.getLinks()) && isNotBlank(response.getLinks().getNext()) &&
                            counter.getAndIncrement() < MAX_PAGES) {

                        var nextUrl = URLDecoder.decode(response.getLinks().getNext(), UTF_8);
                        var query = URI.create(nextUrl).getQuery();

                        var continueToken = Arrays.stream(query.split("&"))
                                .filter(param -> param.contains("token="))
                                .map(param -> param.split("=")[1])
                                .findFirst().orElse(null);

                        return new GetAccessListMembersCommand(webClient, Optional.ofNullable(continueToken), exchangeToken, altinnConfig).call();

                    } else {
                        return Mono.empty();
                    }
                })
                .map(AltinnAccessListResponseDTO::getData)
                .flatMap(Flux::fromIterable)
                .collectList();
    }

    private Flux<Organisasjon> convertToOrganisasjon(List<AltinnAccessListResponseDTO.AccessListMembershipDTO> altInnResponse) {

        return Flux.fromIterable(altInnResponse)
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