package no.nav.dolly.bestilling.brregstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.bestilling.brregstub.mapper.RolleUtskriftMapper;
import no.nav.dolly.bestilling.brregstub.util.BrregstubMergeUtil;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.service.DollyPersonCache;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrregstubClient implements ClientRegister {

    private static final String OK_STATUS = "OK";
    private static final String FEIL_STATUS = "Feil= ";

    private final BrregstubConsumer brregstubConsumer;
    private final RolleUtskriftMapper rolleUtskriftMapper;
    private final DollyPersonCache dollyPersonCache;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBrregstub())) {

            dollyPersonCache.fetchIfEmpty(dollyPerson);

            RolleoversiktTo nyRolleovesikt = rolleUtskriftMapper.map(bestilling.getBrregstub(), dollyPerson);

            RolleoversiktTo eksisterendeRoller = brregstubConsumer.getRolleoversikt(dollyPerson.getHovedperson());
            RolleoversiktTo mergetRolleoversikt = BrregstubMergeUtil.merge(nyRolleovesikt, eksisterendeRoller);

            progress.setBrregstubStatus(postRolleutskrift(mergetRolleoversikt));
        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        brregstubConsumer.deleteRolleoversikt(identer)
                .subscribe(resp -> log.info("Sletting utført i Brregstub"));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getBrregstub()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getBrregstubStatus()));
    }

    private String postRolleutskrift(RolleoversiktTo rolleoversiktTo) {

            var status = brregstubConsumer.postRolleoversikt(rolleoversiktTo);
            return isBlank(status.getError()) ? OK_STATUS : FEIL_STATUS + encodeStatus(status.getError());
    }
}
