package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivClient implements ClientRegister {

    private final DokarkivConsumer dokarkivConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final DollyPersonCache dollyPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getDokarkiv())) {

            StringBuilder status = new StringBuilder();
            DokarkivRequest dokarkivRequest = mapperFacade.map(bestilling.getDokarkiv(), DokarkivRequest.class);

            dollyPersonCache.fetchIfEmpty(dollyPerson);
            dokarkivRequest.getBruker().setId(dollyPerson.getHovedperson());
            Person avsender = dollyPerson.getPerson(dollyPerson.getHovedperson());
            if (isBlank(dokarkivRequest.getAvsenderMottaker().getId())) {
                dokarkivRequest.getAvsenderMottaker().setId(dollyPerson.getHovedperson());
            }
            if (isBlank(dokarkivRequest.getAvsenderMottaker().getNavn())) {
                dokarkivRequest.getAvsenderMottaker().setNavn(String.format("%s, %s%s", avsender.getFornavn(), avsender.getEtternavn(), isNull(avsender.getMellomnavn()) ? "" : ", " + avsender.getMellomnavn()));
            }

            bestilling.getEnvironments().forEach(environment -> {

                if (!transaksjonMappingService.existAlready(DOKARKIV, dollyPerson.getHovedperson(), environment) || isOpprettEndre) {
                    try {
                        DokarkivResponse response = dokarkivConsumer.postDokarkiv(environment, dokarkivRequest).block();
                        if (nonNull(response)) {
                            status.append(isNotBlank(status) ? ',' : "")
                                    .append(environment)
                                    .append(":OK");

                            saveTransaksjonId(response, dollyPerson.getHovedperson(),
                                    progress.getBestilling().getId(), environment);
                        }

                    } catch (RuntimeException e) {

                        status.append(isNotBlank(status) ? ',' : "")
                                .append(environment)
                                .append(':')
                                .append(errorStatusDecoder.decodeRuntimeException(e));

                        log.error("Feilet å legge inn person: {} til Dokarkiv miljø: {}",
                                dokarkivRequest.getBruker().getId(), environment, e);
                    }
                }
            });
            progress.setDokarkivStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    private void saveTransaksjonId(DokarkivResponse response, String ident, Long bestillingId, String miljoe) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(JoarkTransaksjon.builder()
                                .journalpostId(response.getJournalpostId())
                                .dokumentInfoId(response.getDokumenter().get(0).getDokumentInfoId())
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(DOKARKIV.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for dokarkiv", e);
        }
        return null;
    }
}
