package no.nav.dolly.bestilling.dokarkiv;

import static java.util.Objects.nonNull;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivClient implements ClientRegister {

    private final DokarkivConsumer dokarkivConsumer;
    private final TpsfPersonCache tpsfPersonCache;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getDokarkiv())) {

            StringBuilder status = new StringBuilder();
            DokarkivRequest dokarkivRequest = mapperFacade.map(bestilling.getDokarkiv(), DokarkivRequest.class);

            dokarkivRequest.getBruker().setId(tpsPerson.getHovedperson());
            bestilling.getEnvironments().forEach(environment -> {

                try {
                    ResponseEntity<DokarkivResponse> response = dokarkivConsumer.postDokarkiv(environment, dokarkivRequest);
                    if (response.hasBody()) {
                        status.append(',')
                                .append(environment)
                                .append(":OK");
                    }
                } catch (RuntimeException e) {

                    status.append(',')
                            .append(environment)
                            .append(':')
                            .append(errorStatusDecoder.decodeRuntimeException(e));

                    log.error("Feilet å legge inn person: {} til Dokarkiv miljø: {}",
                            dokarkivRequest.getBruker().getId(), environment, e);
                }
            });
            progress.setDokarkivStatus(status.substring(1));
        }
    }

    @Override
    public void release(List<String> identer) {

    }
}
