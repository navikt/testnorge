package no.nav.dolly.bestilling.brregstub;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.bestilling.brregstub.mapper.RolleUtskriftMapper;
import no.nav.dolly.bestilling.brregstub.util.BrregstubMergeUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class BrregstubClient implements ClientRegister {

    private static final String OK_STATUS = "OK";

    private final BrregstubConsumer brregstubConsumer;
    private final RolleUtskriftMapper rolleUtskriftMapper;
    private final ErrorStatusDecoder errorStatusDecoder;
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

        identer.forEach(brregstubConsumer::deleteRolleoversikt);
    }

    private String postRolleutskrift(RolleoversiktTo rolleoversiktTo) {

        try {
            ResponseEntity<RolleoversiktTo> status = brregstubConsumer.postRolleoversikt(rolleoversiktTo);
            if (HttpStatus.CREATED == status.getStatusCode()) {
                return OK_STATUS;
            }
        } catch (RuntimeException e) {

            return errorStatusDecoder.decodeRuntimeException(e);
        }

        return "Uspesifisert feil";
    }
}
