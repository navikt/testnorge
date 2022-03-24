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
import no.nav.pdl.forvalter.dto.HistoriskIdent;
import no.nav.pdl.forvalter.dto.Ordre;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.getBestillingUrl;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SLETTING;

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
                .build();
        this.objectMapper = objectMapper;
    }

    public Flux<OrdreResponseDTO.PdlStatusDTO> send(List<Ordre> orders) {
        return tokenExchange
                .exchange(properties)
                .flatMapMany(accessToken -> Flux.concat(orders
                        .stream()
                        .map(order -> order.apply(accessToken))
                        .collect(Collectors.toList())
                ));
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
            artifact.setMetadata(nonNull(artifact.getMetadata()) ? artifact.getMetadata() : new FolkeregistermetadataDTO());
            artifact.getMetadata().setGjeldende(nonNull(artifact.getMetadata().getGjeldende()) ? artifact.getMetadata().getGjeldende() : artifact.getGjeldende());
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
                                        (HistoriskIdent) value.getBody(),
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
