package no.nav.dolly.bestilling.udistub;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.udistub.UdiStubDefaultPersonUtil.setPersonDefaultsIfUnspecified;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.udistub.model.PersonTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class UdiStubClient implements ClientRegister {

    @Autowired
    private UdiStubConsumer udiStubConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getUdistub())) {
            StringBuilder status = new StringBuilder();

            try {
                ResponseEntity<PersonControllerResponse> deleteResponse = udiStubConsumer.deleteUdiPerson(progress.getBestillingId(), norskIdent.getIdent());

                if (isOkStatus(deleteResponse)) {
                    PersonTo udiPerson = mapperFacade.map(bestilling.getUdistub(), PersonTo.class);
                    udiPerson.setIdent(norskIdent.getIdent());
                    setPersonDefaultsIfUnspecified(udiPerson);

                    ResponseEntity<PersonControllerResponse> postResponse = udiStubConsumer.createUdiPerson(progress.getBestillingId(), udiPerson);

                    appendOkStatus(status, postResponse);
                }
            } catch (RuntimeException e) {
                appendErrorStatus(status, e);
                log.error("Gjenopprett feilet for udistubclient: {}", e.getMessage(), e);
            }
            progress.setUdistubStatus(status.toString());
        }
    }

    private boolean isOkStatus(ResponseEntity<PersonControllerResponse> response) {
        return HttpStatus.OK == response.getStatusCode() ||
                HttpStatus.ACCEPTED == response.getStatusCode();
    }

    private static void appendOkStatus(StringBuilder status, ResponseEntity<PersonControllerResponse> postResponse) {
        if (postResponse.getBody() != null && postResponse.getBody().getPerson() != null) {
            PersonTo createdPerson = postResponse.getBody().getPerson();
            status.append(',')
                    .append("ident=")
                    .append(createdPerson.getIdent())
                    .append(postResponse.getStatusCode().getReasonPhrase());
        }
    }

    private static void appendErrorStatus(StringBuilder status, RuntimeException e) {
        status.append("Feil: ")
                .append(e.getMessage());

        if (e instanceof HttpClientErrorException) {
            status.append(" (")
                    .append(encodeErrorStatus(((HttpClientErrorException) e).getResponseBodyAsString()))
                    .append(')');
        }
    }

    private static String encodeErrorStatus(String toBeEncoded) {
        return toBeEncoded.replaceAll(",", "&").replaceAll(":", "=");
    }
}
