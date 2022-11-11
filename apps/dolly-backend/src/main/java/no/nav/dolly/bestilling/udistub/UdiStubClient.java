package no.nav.dolly.bestilling.udistub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.bestilling.udistub.util.UdiMergeService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubClient implements ClientRegister {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final UdiMergeService udiMergeService;
    private final UdiStubConsumer udiStubConsumer;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getUdistub())) {
            StringBuilder status = new StringBuilder();

            try {
                UdiPersonResponse eksisterendeUdiPerson = udiStubConsumer.getUdiPerson(dollyPerson.getHovedperson());

                UdiPersonWrapper wrapper = udiMergeService.merge(bestilling.getUdistub(), eksisterendeUdiPerson,
                        isOpprettEndre, dollyPerson);

                wrapper.getUdiPerson().setAliaser(udiMergeService.getAliaser(dollyPerson));

                sendUdiPerson(wrapper);
                status.append("OK");

            } catch (RuntimeException e) {
                status.append(errorStatusDecoder.decodeThrowable(e));
            }
            progress.setUdistubStatus(status.toString());
        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

            udiStubConsumer.deleteUdiPerson(identer)
                    .subscribe(response -> log.info("Slettet identer fra Udistub"));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getUdistub()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getUdistubStatus()));
    }

    private void sendUdiPerson(UdiPersonWrapper wrapper) {

        if (Status.NEW == wrapper.getStatus()) {
            udiStubConsumer.createUdiPerson(wrapper.getUdiPerson());
        } else {
            udiStubConsumer.updateUdiPerson(wrapper.getUdiPerson());
        }
    }

    public Map<String, Object> status() {
        return udiStubConsumer.checkStatus();
    }
}
