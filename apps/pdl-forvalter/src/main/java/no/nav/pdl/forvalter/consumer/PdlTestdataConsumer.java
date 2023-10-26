package no.nav.pdl.forvalter.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.PdlServiceProperties;
import no.nav.pdl.forvalter.consumer.command.PdlAktoerNpidCommand;
import no.nav.pdl.forvalter.consumer.command.PdlDeleteCommandPdl;
import no.nav.pdl.forvalter.consumer.command.PdlOpprettArtifactCommandPdl;
import no.nav.pdl.forvalter.consumer.command.PdlOpprettPersonCommandPdl;
import no.nav.pdl.forvalter.dto.ArtifactValue;
import no.nav.pdl.forvalter.dto.OpprettIdent;
import no.nav.pdl.forvalter.dto.OrdreRequest;
import no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.data.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.getBestillingUrl;
import static no.nav.testnav.libs.data.pdlforvalter.v1.PdlArtifact.PDL_SLETTING;

@Slf4j
@Service
public class PdlTestdataConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;
    private final ObjectMapper objectMapper;

    public PdlTestdataConsumer(TokenExchange tokenExchange,
                               PdlServiceProperties properties,
                               ObjectMapper objectMapper) {

        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .filters(exchangeFilterFunctions ->
                    exchangeFilterFunctions.add(logRequest()))
                .build();
        this.objectMapper = objectMapper;
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

    public Flux<OrdreResponseDTO.PdlStatusDTO> send(OrdreRequest orders) {

        return tokenExchange
                .exchange(properties)
                .flatMapMany(accessToken -> Flux.concat(
                                Flux.fromIterable(orders.getSletting())
                                        .parallel()
                                        .flatMap(order -> Flux.fromIterable(order)
                                                .flatMap(entry -> entry.apply(accessToken))
                                                .collectList()),
                                Flux.fromIterable(orders.getOppretting())
                                        .flatMap(order -> Flux.fromIterable(order)
                                                .flatMap(entry -> entry.apply(accessToken))
                                                .collectList()),
                                Flux.fromIterable(orders.getOpplysninger())
                                        .parallel()
                                        .flatMap(order -> Flux.fromIterable(order)
                                                .flatMap(entry -> entry.apply(accessToken))
                                                .collectList()))
                        .flatMap(Flux::fromIterable));
    }

    public Flux<List<OrdreResponseDTO.HendelseDTO>> delete(Set<String> identer) {

        return Flux.from(tokenExchange
                .exchange(properties)
                .flatMapMany(accessToken -> identer
                        .stream()
                        .map(ident -> Flux.from(new PdlDeleteCommandPdl(webClient, getBestillingUrl().get(PDL_SLETTING), ident, accessToken.getTokenValue()).call()))
                        .reduce(Flux.empty(), Flux::concat))
                .collectList());
    }

    public Flux<OrdreResponseDTO.HendelseDTO> send(ArtifactValue value, AccessToken accessToken) {

        String body;
        try {
            var artifact = value.getBody();
            if (isNull(artifact.getFolkeregistermetadata())) {
                artifact.setFolkeregistermetadata(new FolkeregistermetadataDTO());
            }
            artifact.getFolkeregistermetadata().setGjeldende(artifact.getGjeldende());
            body = objectMapper.writeValueAsString(artifact);
        } catch (JsonProcessingException e) {
            return Flux.just(
                    OrdreResponseDTO.HendelseDTO.builder()
                            .id(value.getBody().getId())
                            .status(PdlStatus.FEIL)
                            .error(e.getMessage())
                            .build()
            );
        }

        switch (value.getArtifact()) {
            case PDL_SLETTING:
                return Flux.from(
                        new PdlDeleteCommandPdl(webClient,
                                getBestillingUrl().get(value.getArtifact()),
                                value.getIdent(),
                                accessToken.getTokenValue()
                        ).call());

            case PDL_OPPRETT_PERSON:

                return Identtype.NPID == getIdenttype(value.getIdent()) ?

                        Flux.from(
                                new PdlAktoerNpidCommand(webClient,
                                        value.getIdent(),
                                        accessToken.getTokenValue()
                                ).call()) :

                        Flux.from(
                                new PdlOpprettPersonCommandPdl(webClient,
                                        getBestillingUrl().get(value.getArtifact()),
                                        value.getIdent(),
                                        (OpprettIdent) value.getBody(),
                                        accessToken.getTokenValue()
                                ).call());

            default:
                return Flux.from(
                        new PdlOpprettArtifactCommandPdl(
                                webClient,
                                getBestillingUrl().get(value.getArtifact()),
                                value.getIdent(),
                                body,
                                accessToken.getTokenValue(),
                                value.getBody().getId()
                        ).call());
        }
    }
}
