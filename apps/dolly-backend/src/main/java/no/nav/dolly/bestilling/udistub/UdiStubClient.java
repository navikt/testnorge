package no.nav.dolly.bestilling.udistub;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.bestilling.udistub.util.UdiMergeService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubClient implements ClientRegister {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final UdiMergeService udiMergeService;
    private final UdiStubConsumer udiStubConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getUdistub())) {
            StringBuilder status = new StringBuilder();
            log.info("Bestilling: {}", Json.pretty(bestilling));
            log.info("DollyPerson: {}", Json.pretty(dollyPerson));

            try {
                UdiPersonResponse eksisterendeUdiPerson = udiStubConsumer.getUdiPerson(dollyPerson.getHovedperson());

                UdiPersonWrapper wrapper = udiMergeService.merge(bestilling.getUdistub(), eksisterendeUdiPerson,
                        isOpprettEndre, dollyPerson);

                wrapper.getUdiPerson().setAliaser(udiMergeService.getAliaser(wrapper.getAliasRequest(),
                        bestilling.getEnvironments(), dollyPerson));

                sendUdiPerson(wrapper);
                status.append("OK");

            } catch (RuntimeException e) {
                status.append(errorStatusDecoder.decodeRuntimeException(e));
            }
            progress.setUdistubStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(udiStubConsumer::deleteUdiPerson);
    }

    private void sendUdiPerson(UdiPersonWrapper wrapper) {

        if (Status.NEW == wrapper.getStatus()) {
            udiStubConsumer.createUdiPerson(wrapper.getUdiPerson());
        } else {
            udiStubConsumer.updateUdiPerson(wrapper.getUdiPerson());
        }
    }
}
