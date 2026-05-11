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
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class HendelseIdService {

    private final BestillingProgressRepository bestillingProgressRepository;
    private final JsonMapper jsonMapper;

    public Mono<JsonNode> getOrdreStatus(String ident, String relatertIdent) {

        return bestillingProgressRepository.findHendelseIdFragmentByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException("Ident %s ikke funnet".formatted(ident))))
                .next()
                .flatMap(fragment -> isNotBlank(fragment.getPdlOrdreStatus()) ?
                        Mono.just(fragment) :
                        Mono.error(new NotFoundException("HendelseId for ident %s ikke funnet".formatted(ident))))
                .map(HendelseIdFragment::getPdlOrdreStatus)
                .map(jsonMapper::readTree)
                .flatMap(jsonNode -> isBlank(relatertIdent)
                        ? Mono.just(jsonNode)
                        : findRelasjon(jsonNode, relatertIdent, ident));
    }

    private Mono<JsonNode> findRelasjon(JsonNode jsonNode, String relatertIdent, String ident) {

        return StreamSupport.stream(jsonNode.path("relasjoner").spliterator(), false)
                .filter(relasjon -> relatertIdent.equals(relasjon.path("ident").asString()))
                .findFirst()
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new NotFoundException(
                        "Relatert ident %s ikke funnet for ident %s".formatted(relatertIdent, ident))));
    }

    public Mono<List<JsonNode>> getOrdrerByArtifact(String ident, PdlArtifact pdlArtifact, String relatertIdent) {

        return getOrdreStatus(ident, relatertIdent)
                .map(jsonNode -> {
                    if (Objects.equals(jsonNode.path("ident").asString(), relatertIdent)) {
                        return jsonNode;
                    } else {
                        return jsonNode.path("hovedperson");
                    }
                })
                .map(jsonNode -> jsonNode.path("ordrer"))
                .map(ordrer -> StreamSupport.stream(ordrer.spliterator(), false)
                        .filter(node -> pdlArtifact.name().equals(node.path("infoElement").asString()))
                        .toList());
    }

    public Mono<JsonNode> getHendelseById(String ident, PdlArtifact pdlArtifact, Integer id, String relatertIdent) {

        return getOrdrerByArtifact(ident, pdlArtifact, relatertIdent)
                .flatMapMany(Flux::fromIterable)
                .flatMapIterable(jsonNode -> jsonNode.path("hendelser"))
                .filter(node -> id.equals(node.path("id").asInt()))
                .next()
                .switchIfEmpty(Mono.error(new NotFoundException("Hendelse med id %d ikke funnet for ident %s og opplysningstype %s".formatted(id,
                        isBlank(relatertIdent) ? ident : relatertIdent, pdlArtifact.name()))));
    }
}
