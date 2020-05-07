package no.nav.dolly.bestilling.bregstub;

import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.bregstub.domain.BrregRequestWrapper;
import no.nav.dolly.bestilling.bregstub.domain.OrganisasjonTo;
import no.nav.dolly.bestilling.bregstub.domain.RolleoversiktTo;
import no.nav.dolly.bestilling.bregstub.mapper.RolleUtskriftMapper;
import no.nav.dolly.bestilling.bregstub.util.BregstubMergeUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;

@Service
@RequiredArgsConstructor
public class BregstubClient implements ClientRegister {

    private static final String OK_STATUS = "OK";
    private final BregstubConsumer bregstubConsumer;
    private final RolleUtskriftMapper rolleUtskriftMapper;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBregstub())) {

            BrregRequestWrapper wrapper = rolleUtskriftMapper.map(bestilling.getBregstub(), tpsPerson);

            RolleoversiktTo mergetRolleoversikt = prepareRequest(wrapper.getRolleoversiktTo(), tpsPerson.getHovedperson());
            String status = postRolleutskrift(mergetRolleoversikt);

            progress.setBregstubStatus(OK_STATUS.equals(status) ? postOrganisasjon(wrapper.getOrganisasjonTo()) : status);
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(bregstubConsumer::deleteRolleoversikt);
    }

    private RolleoversiktTo prepareRequest(RolleoversiktTo nyRolleovesikt, String ident) {

        RolleoversiktTo eksisterendeRoller = bregstubConsumer.getRolleoversikt(ident).getBody();

        return BregstubMergeUtil.merge(nyRolleovesikt, eksisterendeRoller);
    }

    private String postRolleutskrift(RolleoversiktTo rolleoversiktTo) {

        try {
            ResponseEntity status = bregstubConsumer.postRolleoversikt(rolleoversiktTo);
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
            organisasjonTo.forEach(bregstubConsumer::postOrganisasjon);
            return OK_STATUS;

        } catch (RuntimeException e) {

            return errorStatusDecoder.decodeRuntimeException(e);
        }
    }
}
