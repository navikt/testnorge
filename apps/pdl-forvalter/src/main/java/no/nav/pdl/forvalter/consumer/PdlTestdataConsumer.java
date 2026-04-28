package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.PdlDeleteCommandPdl;
import no.nav.pdl.forvalter.consumer.command.PdlDeleteHendelseIdCommandPdl;
import no.nav.pdl.forvalter.consumer.command.PdlMergeNpidCommand;
import no.nav.pdl.forvalter.consumer.command.PdlOpprettArtifactCommandPdl;
import no.nav.pdl.forvalter.consumer.command.PdlOpprettNpidCommand;
import no.nav.pdl.forvalter.consumer.command.PdlOpprettPersonCommandPdl;
import no.nav.pdl.forvalter.dto.ArtifactValue;
import no.nav.pdl.forvalter.dto.HendelseIdRequest;
import no.nav.pdl.forvalter.dto.MergeIdent;
import no.nav.pdl.forvalter.dto.OpprettIdent;
import no.nav.pdl.forvalter.dto.OrdreRequest;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.getBestillingUrl;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SLETTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SLETTING_HENDELSEID;

@Slf4j
@Service
public class PdlTestdataConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final ObjectMapper objectMapper;
    private final PersonServiceConsumer personServiceConsumer;

    public PdlTestdataConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            ObjectMapper objectMapper,
            PersonServiceConsumer personServiceConsumer,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getPdlProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .filters(exchangeFilterFunctions -> exchangeFilterFunctions.add(logRequest()))
                .build();
        this.objectMapper = objectMapper;
        this.personServiceConsumer = personServiceConsumer;
    }

    public Flux<OrdreResponseDTO.PdlStatusDTO> send(OrdreRequest orders) {

        return tokenExchange
                .exchange(serverProperties)
                .flatMapMany(accessToken -> Flux.concat(
                        Flux.fromIterable(orders.getSletting())
                                .parallel()
                                .flatMap(order -> order.apply(accessToken)),
                        Flux.fromIterable(orders.getOppretting())
                                .concatMap(order -> order.apply(accessToken)),
                        Flux.fromIterable(orders.getMerge())
                                .concatMap(order -> order.apply(accessToken)),
                        Flux.fromIterable(orders.getOpplysninger())
                                .parallel()
                                .flatMap(order -> order.apply(accessToken))
                ));
    }

    public Mono<List<OrdreResponseDTO.HendelseDTO>> delete(Set<String> identer) {

        return tokenExchange
                .exchange(serverProperties)
                .flatMapMany(accessToken -> Flux.fromIterable(identer)
                        .flatMap(ident -> new PdlDeleteCommandPdl(webClient,
                                getBestillingUrl().get(PDL_SLETTING), ident, accessToken.getTokenValue()).call()))
                .collectList();
    }

    public Flux<OrdreResponseDTO.HendelseDTO> deleteHendelse(HendelseIdRequest hendelse) {

        return tokenExchange
                .exchange(serverProperties)
                .flatMapMany(accessToken -> new PdlDeleteHendelseIdCommandPdl(webClient,
                        getBestillingUrl().get(PDL_SLETTING_HENDELSEID),
                        hendelse.getIdent(), hendelse.getHendelseId(), accessToken.getTokenValue()).call());
    }

    public Mono<OrdreResponseDTO.HendelseDTO> deleteHendelse(String ident, String hendelseId) {

        return tokenExchange
                .exchange(serverProperties)
                .flatMapMany(accessToken -> new PdlDeleteHendelseIdCommandPdl(webClient,
                        getBestillingUrl().get(PDL_SLETTING_HENDELSEID),
                        ident, hendelseId, accessToken.getTokenValue()).call())
                .collectList()
                .map(List::getFirst);
    }

    public Flux<OrdreResponseDTO.HendelseDTO> send(ArtifactValue value, AccessToken accessToken) {

        String body;
        try {
            var artifact = value.getBody();
            if (isNull(artifact.getFolkeregistermetadata())) {
                artifact.setFolkeregistermetadata(new FolkeregistermetadataDTO());
            }
            body = objectMapper.writeValueAsString(artifact);
        } catch (JacksonException e) {
            return Flux.just(
                    OrdreResponseDTO.HendelseDTO.builder()
                            .id(value.getBody().getId())
                            .status(PdlStatus.FEIL)
                            .error(e.getMessage())
                            .build()
            );
        }

        return switch (value.getArtifact()) {
            case PDL_SLETTING -> new PdlDeleteCommandPdl(webClient,
                    getBestillingUrl().get(value.getArtifact()),
                    value.getIdent(),
                    accessToken.getTokenValue()
            ).call();

            case PDL_SLETTING_HENDELSEID -> new PdlDeleteHendelseIdCommandPdl(webClient,
                    getBestillingUrl().get(value.getArtifact()),
                    value.getIdent(),
                    value.getBody().getHendelseId(),
                    accessToken.getTokenValue()
            ).call();

            case PDL_OPPRETT_PERSON -> Identtype.NPID == getIdenttype(value.getIdent()) ?

                    new PdlOpprettNpidCommand(webClient,
                            value.getIdent(),
                            accessToken.getTokenValue()
                    ).call() :

                    new PdlOpprettPersonCommandPdl(webClient,
                            getBestillingUrl().get(value.getArtifact()),
                            value.getIdent(),
                            (OpprettIdent) value.getBody(),
                            accessToken.getTokenValue()
                    ).call();

            case PDL_PERSON_MERGE -> personServiceConsumer.syncIdent(value.getIdent())
                    .flatMap(syncIdent ->
                            new PdlMergeNpidCommand(webClient,
                                    getBestillingUrl().get(value.getArtifact()),
                                    ((MergeIdent) value.getBody()).getNpid(),
                                    value.getIdent(),
                                    accessToken.getTokenValue()
                            ).call());

            default -> !isTestnorgeIdent(value.getIdent()) || value.getBody().getMaster() == Master.PDL ?
                    new PdlOpprettArtifactCommandPdl(
                            webClient,
                            getBestillingUrl().get(value.getArtifact()),
                            value.getIdent(),
                            body,
                            accessToken.getTokenValue(),
                            value.getBody().getId()
                    ).call() :
                    Flux.empty();
        };
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
}
