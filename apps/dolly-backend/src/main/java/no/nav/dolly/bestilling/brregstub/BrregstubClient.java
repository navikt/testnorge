package no.nav.dolly.bestilling.brregstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.bestilling.brregstub.mapper.RolleUtskriftMapper;
import no.nav.dolly.bestilling.brregstub.util.BrregstubMergeUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.service.DollyPersonCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBrregstub())) {

            dollyPersonCache.fetchIfEmpty(dollyPerson);

            RolleoversiktTo nyRolleovesikt = rolleUtskriftMapper.map(bestilling.getBrregstub(), dollyPerson);

            RolleoversiktTo eksisterendeRoller = brregstubConsumer.getRolleoversikt(dollyPerson.getHovedperson());
            RolleoversiktTo mergetRolleoversikt = BrregstubMergeUtil.merge(nyRolleovesikt, eksisterendeRoller);

            progress.setBrregstubStatus(postRolleutskrift(mergetRolleoversikt));
        }
    }

    @Override
    public void release(List<String> identer) {

        brregstubConsumer.deleteRolleoversikt(identer)
                .subscribe(resp -> log.info("Sletting utført i Brregstub"));
    }

    private String postRolleutskrift(RolleoversiktTo rolleoversiktTo) {

            var status = brregstubConsumer.postRolleoversikt(rolleoversiktTo);
            return isBlank(status.getError()) ? OK_STATUS : FEIL_STATUS + encodeStatus(status.getError());
    }

    public Map<String, Object> status() {
        return brregstubConsumer.checkStatus();
    }
}
