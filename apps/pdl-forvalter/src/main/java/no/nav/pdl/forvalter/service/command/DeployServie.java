package no.nav.pdl.forvalter.service.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;
import no.nav.pdl.forvalter.utils.PdlTestDataUrls;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlStatus.FEIL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployServie {

    private static final String PDL_ERROR_TEXT = "Feil ved skriving av PDL-testdata. {}";

    private final PdlTestdataConsumer pdlTestdataConsumer;

    public List<PdlOrdreResponse.PdlStatus> put(PdlTestDataUrls.PdlArtifact type,
                                                String ident,
                                                List<? extends PdlDbVersjon> artifact) {

        var status = new ArrayList<PdlOrdreResponse.Hendelse>();
        if (!artifact.isEmpty()) {
            artifact.stream()
                    .collect(Collectors.toCollection(LinkedList::new))
                    .descendingIterator()
                    .forEachRemaining(element -> {
                                try {
                                    status.add(PdlOrdreResponse.Hendelse.builder()
                                            .id(element.getId())
                                            .status(OK)
                                            .hendelseId(pdlTestdataConsumer.sendArtifactToPdl(type, ident, element)
                                                    .getHendelseId())
                                            .build());
                                } catch (HttpClientErrorException e) {
                                    status.add(PdlOrdreResponse.Hendelse.builder()
                                            .id(element.getId())
                                            .status(FEIL)
                                            .error(e.getResponseBodyAsString())
                                            .build());
                                    log.error(PDL_ERROR_TEXT, e.getResponseBodyAsString());
                                } catch (WebClientResponseException e) {
                                    status.add(PdlOrdreResponse.Hendelse.builder()
                                            .id(element.getId())
                                            .status(FEIL)
                                            .error(e.getResponseBodyAsString())
                                            .build());
                                    log.error(PDL_ERROR_TEXT, e.getResponseBodyAsString());
                                } catch (JsonProcessingException e) {
                                    status.add(PdlOrdreResponse.Hendelse.builder()
                                            .id(element.getId())
                                            .status(FEIL)
                                            .error(e.getMessage())
                                            .build());
                                    log.error(PDL_ERROR_TEXT, e.getMessage(), e);
                                }
                            }
                    );
        }

        return status.isEmpty() ? emptyList() : List.of(PdlOrdreResponse.PdlStatus.builder()
                .infoElement(type)
                .hendelser(status)
                .build());
    }

    public List<PdlOrdreResponse.PdlStatus> create(String ident) {
        pdlTestdataConsumer.createPerson(ident);
        return null;
    }

    public List<PdlOrdreResponse.PdlStatus> delete(String ident) {
        pdlTestdataConsumer.deletePerson(ident);
        return null;
    }
}
