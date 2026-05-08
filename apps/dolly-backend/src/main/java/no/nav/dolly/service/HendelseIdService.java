package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.projection.HendelseIdFragment;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class HendelseIdService {

    private final BestillingProgressRepository bestillingProgressRepository;
    private final JsonMapper jsonMapper;

    public Mono<JsonNode> getHendelserForIdent(String ident) {

        return bestillingProgressRepository.findHendelseIdFragmentByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException("Ident %s ikke funnet".formatted(ident))))
                .next()
                .flatMap(fragment -> isNotBlank(fragment.getPdlOrdreStatus()) ?
                        Mono.just(fragment) :
                        Mono.error(new NotFoundException("HendelseId for ident %s ikke funnet".formatted(ident))))
                .map(HendelseIdFragment::getPdlOrdreStatus)
                .map(jsonMapper::readTree);
    }

    public Mono<JsonNode> getHendelserForIdent(String ident, PdlArtifact pdlArtifact) {

        return getHendelserForIdent(ident)
                .map(jsonNode -> jsonNode.path("hovedperson").path("ordrer"))
                .flatMap(ordrer -> {
                    if (ordrer.isArray()) {
                        for (JsonNode node : ordrer) {
                            if (pdlArtifact.name().equals(node.path("infoElement").asString())) {
                                return Mono.just(node);
                            }
                        }
                    }
                    return Mono.empty();
                });
    }

    public Mono<JsonNode> getHendelserForIdent(String ident, PdlArtifact pdlArtifact, Integer id) {

        return getHendelserForIdent(ident, pdlArtifact)
                .map(jsonNode -> jsonNode.path("hendelser"))
                .flatMap(hendelser -> {
                    if (hendelser.isArray()) {
                        for (JsonNode node : hendelser) {
                            if (id.equals(node.path("id").asInt())) {
                                return Mono.just(node);
                            }
                        }
                    }
                    return Mono.empty();
                });
    }
}
