package no.nav.pdl.forvalter.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.PdlServiceProperties;
import no.nav.pdl.forvalter.consumer.command.PdlDeleteCommandPdl;
import no.nav.pdl.forvalter.consumer.command.PdlOpprettArtifactCommandPdl;
import no.nav.pdl.forvalter.consumer.command.PdlOpprettPersonCommandPdl;
import no.nav.pdl.forvalter.domain.ArtifactValue;
import no.nav.pdl.forvalter.domain.Ordre;
import no.nav.pdl.forvalter.dto.HistoriskIdent;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.getBestillingUrl;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SLETTING;

@Slf4j
@Service
public class PdlTestdataConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final ServerProperties properties;
    private final ObjectMapper objectMapper;

    public PdlTestdataConsumer(AccessTokenService accessTokenService,
                               PdlServiceProperties properties,
                               ObjectMapper objectMapper) {

        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
        this.objectMapper = objectMapper;
    }

    public Flux<OrdreResponseDTO.PdlStatusDTO> send(List<Ordre> orders) {
        return accessTokenService
                .generateToken(properties)
                .flatMapMany(accessToken -> Flux.concat(orders
                        .stream()
                        .map(order -> order.apply(accessToken))
                        .collect(Collectors.toList())
                ));
    }

    public Flux<List<OrdreResponseDTO.HendelseDTO>> delete(List<String> identer) {

        return Flux.from(accessTokenService
                .generateToken(properties)
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
                return Flux.from(
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
