package no.nav.dolly.bestilling.udistub;

import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.util.UdiMergeService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubClient implements ClientRegister {

    private final UdiStubConsumer udiStubConsumer;
    private final MapperFacade mapperFacade;
    private final UdiMergeService udiMergeService;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getUdistub())) {
            StringBuilder status = new StringBuilder();

            try {

                UdiPersonResponse eksisterendeUdiPerson = udiStubConsumer.getUdiPerson(tpsPerson.getHovedperson());

                UdiPersonWrapper mergetUdiPerson = udiMergeService.merge(bestilling,
                        eksisterendeUdiPerson, isOpprettEndre, tpsPerson);

                ResponseEntity<UdiPersonResponse> response = udiStubConsumer.createUdiPerson(udiPerson);
                appendOkStatus(status, response);

            } catch (RuntimeException e) {

                appendErrorStatus(status, e);
                log.error("Gjenopprett feilet for udistubclient: {}", e.getMessage(), e);
            }

            progress.setUdistubStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(udiStubConsumer::deleteUdiPerson);
    }

    private static void appendOkStatus(StringBuilder status, ResponseEntity<UdiPersonResponse> postResponse) {
        if (nonNull(postResponse) && postResponse.hasBody()) {
            status.append("OK");
        }
    }

    private static void appendErrorStatus(StringBuilder status, RuntimeException e) {
        status.append("FEIL: ");

        if (e instanceof HttpClientErrorException) {
            status.append(" (")
                    .append(((HttpClientErrorException) e).getResponseBodyAsString())
                    .append(')');

        } else if (e instanceof HttpServerErrorException) {
            status.append(" Teknisk feil i UdiStub. Se logg!");

        }
    }
}
