package no.nav.dolly.bestilling.tpsmessagingservice;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.KontaktOpplysninger;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.KontaktopplysningRequest;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final ResponseHandler responseHandler;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        log.info("Bestilling: {}", Json.pretty(bestilling));
        if (nonNull(bestilling.getTpsMessaging()))

            try {
                KontaktopplysningRequest kontaktopplysningRequest = new KontaktopplysningRequest(
                        dollyPerson.getHovedperson(),
                        bestilling.getEnvironments(),
                        mapperFacade.map(bestilling.getTpsMessaging(), KontaktOpplysninger.class)
                );

                ResponseEntity<Object> tpsMessagingResponse = tpsMessagingConsumer.createTpsKontaktopplysninger(kontaktopplysningRequest);
                progress.setTpsImportStatus(responseHandler.extractResponse(tpsMessagingResponse));

            } catch (RuntimeException e) {

                progress.setTpsImportStatus(errorStatusDecoder.decodeRuntimeException(e));
                log.error("Kall til TPS messaging service feilet: {}", e.getMessage(), e);
            }
    }

    @Override
    public void release(List<String> identer) {

        throw new UnsupportedOperationException("Release ikke implementert");
    }
}
