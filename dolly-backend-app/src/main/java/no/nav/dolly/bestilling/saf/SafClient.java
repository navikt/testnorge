package no.nav.dolly.bestilling.saf;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.bestilling.saf.domain.SafRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SafClient implements ClientRegister {

    private final SafConsumer safConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final TpsfPersonCache tpsfPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getDokarkiv())) {

            StringBuilder status = new StringBuilder();
            SafRequest safRequest = mapperFacade.map(bestilling.getDokarkiv(), SafRequest.class);

            tpsfPersonCache.fetchIfEmpty(tpsPerson);

            bestilling.getEnvironments().forEach(environment -> {

                if (!transaksjonMappingService.existAlready(DOKARKIV, tpsPerson.getHovedperson(), environment) || isOpprettEndre) {
                    try {
                        ResponseEntity<String> response = safConsumer.getDokument(environment, safRequest);
                        if (response.hasBody()) {
                            status.append(',')
                                    .append(environment)
                                    .append(":OK");

                            saveTranskasjonId(response, environment);
                        }

                    } catch (RuntimeException e) {

                        status.append(',')
                                .append(environment)
                                .append(':')
                                .append(errorStatusDecoder.decodeRuntimeException(e));

                        log.error("Feilet å hente dokument fra SAF i miljø: {}",
                                environment, e);
                    }
                }
            });
            progress.setDokarkivStatus(status.length() > 0 ? status.substring(1) : null);
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    private void saveTranskasjonId(ResponseEntity<String> response, String miljoe) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .transaksjonId(toJson(JoarkTransaksjon.builder()
                                .build())) // TODO FIKSE
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
