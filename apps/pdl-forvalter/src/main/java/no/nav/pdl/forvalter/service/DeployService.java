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

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployService {

    private final PdlTestdataConsumer pdlTestdataConsumer;

    public List<Ordre> createOrder(PdlArtifact type, String ident, DbVersjonDTO artifact) {

        return createOrder(type, ident, List.of(artifact), true);
    }

    public List<Ordre> createOrder(PdlArtifact type, String ident, List<? extends DbVersjonDTO> artifacter, boolean flere) {

        return flere && artifacter.size() > 1 || !flere && artifacter.size() < 2 ?

                artifacter.stream()
                        .sorted(Comparator.comparing(DbVersjonDTO::getId))
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
                        .toList() :
                emptyList();
    }

    public Flux<OrdreResponseDTO.PdlStatusDTO> sendOrders(OrdreRequest ordres) {

        return pdlTestdataConsumer.send(ordres);
    }
}