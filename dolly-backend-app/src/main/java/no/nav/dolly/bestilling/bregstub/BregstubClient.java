package no.nav.dolly.bestilling.bregstub;

import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.bregstub.domain.RolleoversiktTo;
import no.nav.dolly.bestilling.bregstub.mapper.RolleUtskriftMapper;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;

@Service
@RequiredArgsConstructor
public class BregstubClient implements ClientRegister {

    private final BregstubConsumer bregstubConsumer;
    private final RolleUtskriftMapper rolleUtskriftMapper;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final ObjectMapper objectMapper;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBregstub())) {

            RolleoversiktTo rolleoversiktTo = rolleUtskriftMapper.map(bestilling.getBregstub(), tpsPerson);
            bregstubConsumer.deleteGrunndata(tpsPerson.getHovedperson());

            progress.setBregstubStatus(postRolleutskrift(rolleoversiktTo));
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(bregstubConsumer::deleteGrunndata);
    }

    private String postRolleutskrift(RolleoversiktTo rolleoversiktTo) {

        try {
            ResponseEntity status = bregstubConsumer.postGrunndata(rolleoversiktTo);
            if (HttpStatus.CREATED == status.getStatusCode()) {
                return "OK";
            }
        } catch (RuntimeException e) {

            return errorStatusDecoder.decodeRuntimeException(e);
        }

        return "Uspesifisert feil";
    }
}
