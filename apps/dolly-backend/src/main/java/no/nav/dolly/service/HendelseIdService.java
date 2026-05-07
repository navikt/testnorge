package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class HendelseIdService {

    private final BestillingProgressRepository bestillingProgressRepository;
    private final JsonMapper jsonMapper;

    public Mono<JsonNode> getHendelserForIdent(String ident) {

        return bestillingProgressRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException("Ident %s ikke funnet".formatted(ident))))
                .sort(Comparator.comparing(BestillingProgress::getId).reversed())
                .next()
                .map(BestillingProgress::getPdlOrdreStatus)
                .map(jsonMapper::readTree);
    }

    public Mono<JsonNode> getHendelserForIdent(String ident, PdlArtifact pdlArtifact) {

        return getHendelserForIdent(ident)
                .map(jsonNode -> jsonNode.get("hovedperson").get("ordrer"))
                .flatMap(ordrer -> {
                    for (JsonNode node : ordrer) {
                        if (pdlArtifact.name().equals(node.get("infoElement").asString())){
                            return Mono.just(node);
                        }
                    }
                    return Mono.empty();
                });
    }

    public Mono<JsonNode> getHendelserForIdent(String ident, PdlArtifact pdlArtifact, Integer id) {

        return getHendelserForIdent(ident, pdlArtifact)
                .map(jsonNode -> jsonNode.get("hendelser"))
                .flatMap(hendelser -> {
                    for (JsonNode node : hendelser) {
                        if (id.equals(node.get("id").asInt())){
                            return Mono.just(node);
                        }
                    }
                    return Mono.empty();
                });
    }
}
