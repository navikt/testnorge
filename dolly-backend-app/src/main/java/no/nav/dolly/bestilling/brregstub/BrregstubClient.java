package no.nav.dolly.bestilling.brregstub;

import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.brregstub.domain.BrregRequestWrapper;
import no.nav.dolly.bestilling.brregstub.domain.OrganisasjonTo;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.bestilling.brregstub.mapper.RolleUtskriftMapper;
import no.nav.dolly.bestilling.brregstub.util.BrregstubMergeUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;

@Service
@RequiredArgsConstructor
public class BrregstubClient implements ClientRegister {

    private static final String OK_STATUS = "OK";

    private final BrregstubConsumer brregstubConsumer;
    private final RolleUtskriftMapper rolleUtskriftMapper;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBrregstub())) {

            BrregRequestWrapper wrapper = rolleUtskriftMapper.map(bestilling.getBrregstub(), tpsPerson);

            RolleoversiktTo mergetRolleoversikt = prepareRequest(wrapper.getRolleoversiktTo(), tpsPerson.getHovedperson());
            String status = postRolleutskrift(mergetRolleoversikt);

            progress.setBrregstubStatus(OK_STATUS.equals(status) ? postOrganisasjon(wrapper.getOrganisasjonTo()) : status);
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(brregstubConsumer::deleteRolleoversikt);
    }

    private RolleoversiktTo prepareRequest(RolleoversiktTo nyRolleovesikt, String ident) {

        RolleoversiktTo eksisterendeRoller = brregstubConsumer.getRolleoversikt(ident).getBody();

        return BrregstubMergeUtil.merge(nyRolleovesikt, eksisterendeRoller);
    }

    private String postRolleutskrift(RolleoversiktTo rolleoversiktTo) {

        try {
            ResponseEntity status = brregstubConsumer.postRolleoversikt(rolleoversiktTo);
            if (HttpStatus.CREATED == status.getStatusCode()) {
                return OK_STATUS;
            }
        } catch (RuntimeException e) {

            return errorStatusDecoder.decodeRuntimeException(e);
        }

        return "Uspesifisert feil";
    }

    private String postOrganisasjon(List<OrganisasjonTo> organisasjonTo) {

        try {
            organisasjonTo.forEach(brregstubConsumer::postOrganisasjon);
            return OK_STATUS;

        } catch (RuntimeException e) {

            return errorStatusDecoder.decodeRuntimeException(e);
        }
    }
}
