package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.domain.ArtifactValue;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PdlArtifact;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployService {

    private final PdlTestdataConsumer pdlTestdataConsumer;

    public Flux<OrdreResponseDTO.PdlStatusDTO> send(PdlArtifact type,
                                                    String ident,
                                                    List<? extends DbVersjonDTO> artifacter) {

        return artifacter.isEmpty() ?
                Flux.empty() :
                pdlTestdataConsumer
                        .send(artifacter.stream()
                                .map(element -> ArtifactValue.builder()
                                        .artifact(type)
                                        .ident(ident)
                                        .body(element)
                                        .build())
                                .collect(Collectors.toList()))
                        .collectList()
                        .flatMapMany(value -> Flux.fromStream(
                                List.of(OrdreResponseDTO.PdlStatusDTO.builder().infoElement(type).hendelser(value).build()).stream())
                        );
    }
}
