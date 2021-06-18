package no.nav.pdl.forvalter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.domain.DbVersjonDTO;
import no.nav.pdl.forvalter.domain.OrdreResponseDTO;
import no.nav.pdl.forvalter.domain.PdlArtifact;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static no.nav.pdl.forvalter.domain.PdlStatus.FEIL;
import static no.nav.pdl.forvalter.domain.PdlStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployService {

    private static final String PDL_ERROR_TEXT = "Feil ved skriving av data {} status: {}";

    private final PdlTestdataConsumer pdlTestdataConsumer;

    public List<OrdreResponseDTO.PdlStatusDTO> send(PdlArtifact type,
                                                    String ident,
                                                    List<? extends DbVersjonDTO> artifact) {

        var status = new ArrayList<OrdreResponseDTO.HendelseDTO>();
        if (!artifact.isEmpty()) {
            artifact.stream()
                    .collect(Collectors.toCollection(LinkedList::new))
                    .descendingIterator()
                    .forEachRemaining(element -> {
                                try {
                                    var response = pdlTestdataConsumer.send(type, ident, element);
                                    status.add(OrdreResponseDTO.HendelseDTO.builder()
                                            .id(element.getId())
                                            .status(OK)
                                            .hendelseId(response.getHendelseId())
                                            .deletedOpplysninger(response.getDeletedOpplysninger())
                                            .build());
                                } catch (WebClientResponseException e) {
                                    status.add(OrdreResponseDTO.HendelseDTO.builder()
                                            .id(element.getId())
                                            .status(FEIL)
                                            .error(e.getResponseBodyAsString())
                                            .build());
                                    log.error(PDL_ERROR_TEXT, type, e.getResponseBodyAsString());
                                } catch (JsonProcessingException e) {
                                    status.add(OrdreResponseDTO.HendelseDTO.builder()
                                            .id(element.getId())
                                            .status(FEIL)
                                            .error(e.getMessage())
                                            .build());
                                    log.error(PDL_ERROR_TEXT, type, e.getMessage(), e);
                                }
                            }
                    );
        }

        return status.isEmpty() ? emptyList() : List.of(OrdreResponseDTO.PdlStatusDTO.builder()
                .infoElement(type)
                .hendelser(status)
                .build());
    }
}
