package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.dto.ArtifactValue;
import no.nav.pdl.forvalter.dto.Ordre;
import no.nav.pdl.forvalter.dto.OrdreRequest;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployService {

    private final PdlTestdataConsumer pdlTestdataConsumer;

    public List<Ordre> createOrdre(PdlArtifact type, String ident, List<? extends DbVersjonDTO> artifacter) {

        return setOpprettet(artifacter).stream()
                .map(element -> ArtifactValue.builder()
                        .artifact(type)
                        .ident(ident)
                        .body(element)
                        .build())
                .map(value -> (Ordre) accessToken ->
                        pdlTestdataConsumer.send(value, accessToken)
                                .collectList()
                                .map(hendelser -> OrdreResponseDTO.PdlStatusDTO
                                        .builder()
                                        .ident(ident)
                                        .infoElement(type)
                                        .hendelser(hendelser)
                                        .build()))
                .toList();
    }

    public Flux<OrdreResponseDTO.PdlStatusDTO> sendOrders(OrdreRequest ordres) {

        return pdlTestdataConsumer.send(ordres);
    }

    private List<? extends DbVersjonDTO> setOpprettet(List<? extends DbVersjonDTO> artifacter) {

        var max = artifacter.stream()
                .filter(artifact -> nonNull(artifact.getId()))
                .mapToInt(DbVersjonDTO::getId)
                .max()
                .orElse(1);

        artifacter.stream()
                .filter(artifact -> nonNull(artifact.getId()))
                .forEach(artifact -> artifact.setOpprettet(
                        Instant.now().minusSeconds((max - artifact.getId()) * 10L)));

        return artifacter;
    }
}