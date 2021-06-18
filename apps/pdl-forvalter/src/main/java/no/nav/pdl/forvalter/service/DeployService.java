package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.domain.ArtifactValues;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;
import no.nav.pdl.forvalter.utils.PdlTestDataUrls;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployService {

    private static final String PDL_ERROR_TEXT = "Feil ved skriving av data {} status: {}";

    private final PdlTestdataConsumer pdlTestdataConsumer;

    public Flux<PdlOrdreResponse.PdlStatus> send(PdlTestDataUrls.PdlArtifact type,
                                                 String ident,
                                                 List<? extends PdlDbVersjon> artifact) {
        var artifactList = artifact.stream().map(element -> new ArtifactValues(type, ident, element)).collect(Collectors.toList());
        return pdlTestdataConsumer
                .send(artifactList)
                .collectList()
                .flatMapMany(value -> Flux.fromStream(
                        List.of(PdlOrdreResponse.PdlStatus.builder().infoElement(type).hendelser(value).build()).stream())
                );
    }
}
