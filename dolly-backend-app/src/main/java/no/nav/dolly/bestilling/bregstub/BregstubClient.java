package no.nav.dolly.bestilling.bregstub;

import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.bregstub.domain.RolleutskriftTo;
import no.nav.dolly.bestilling.bregstub.mapper.RolleUtskriftMapper;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Service
@RequiredArgsConstructor
public class BregstubClient implements ClientRegister {

    private final BregstubConsumer bregstubConsumer;
    private final RolleUtskriftMapper rolleUtskriftMapper;

    @Override public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBregstub())) {

            StringBuilder status = new StringBuilder();

            RolleutskriftTo rolleutskriftTo = rolleUtskriftMapper.map(bestilling.getBregstub(), tpsPerson);
            postRolleutskrift(rolleutskriftTo);

        }
    }

    @Override public void release(List<String> identer) {

    }

    private String postRolleutskrift(RolleutskriftTo rolleutskriftTo) {

        try {
            ResponseEntity status = bregstubConsumer.postGrunndata(rolleutskriftTo);
            if (HttpStatus.CREATED == status.getStatusCode()) {
                return "OK";
            }
        } catch (HttpClientErrorException e) {}
        return "Uspesifisert feil";
    }
}
