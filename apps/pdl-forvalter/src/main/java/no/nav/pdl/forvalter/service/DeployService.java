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

    public Flux<Ordre> createOrdre(PdlArtifact type, String ident, List<? extends DbVersjonDTO> artifacter) {

        return Flux.fromIterable(setOpprettet(artifacter))
                .map(element -> ArtifactValue.builder()
                        .artifact(type)
                        .ident(ident)
                        .body(element)
                        .build())
                .map(value ->accessToken ->
                        pdlTestdataConsumer.send(value, accessToken)
                                .map(hendelse -> OrdreResponseDTO.PdlStatusDTO
                                        .builder()
                                        .ident(ident)
                                        .infoElement(type)
                                        .hendelser(List.of(hendelse))
                                        .build()));
    }

    public Flux<OrdreResponseDTO.PdlStatusDTO> sendOrders(OrdreRequest ordre) {

        return pdlTestdataConsumer.send(ordre);
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