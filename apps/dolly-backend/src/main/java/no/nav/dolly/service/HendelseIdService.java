package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.projection.HendelseIdFragment;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class HendelseIdService {

    private final BestillingProgressRepository bestillingProgressRepository;
    private final JsonMapper jsonMapper;

    public Mono<JsonNode> getOrdreStatus(String ident) {

        return bestillingProgressRepository.findHendelseIdFragmentByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException("Ident %s ikke funnet".formatted(ident))))
                .next()
                .flatMap(fragment -> isNotBlank(fragment.getPdlOrdreStatus()) ?
                        Mono.just(fragment) :
                        Mono.error(new NotFoundException("HendelseId for ident %s ikke funnet".formatted(ident))))
                .map(HendelseIdFragment::getPdlOrdreStatus)
                .map(jsonMapper::readTree);
    }

    public Mono<List<JsonNode>> getOrdrerByArtifact(String ident, PdlArtifact pdlArtifact) {

        return getOrdreStatus(ident)
                .map(jsonNode -> jsonNode.path("hovedperson").path("ordrer"))
                .map(ordrer -> StreamSupport.stream(ordrer.spliterator(), false)
                        .filter(node -> pdlArtifact.name().equals(node.path("infoElement").asString()))
                        .toList());
    }

    public Mono<JsonNode> getHendelseById(String ident, PdlArtifact pdlArtifact, Integer id) {

        return getOrdrerByArtifact(ident, pdlArtifact)
                .flatMapMany(Flux::fromIterable)
                .flatMapIterable(jsonNode -> jsonNode.path("hendelser"))
                .filter(node -> id.equals(node.path("id").asInt()))
                .next();
    }
}
