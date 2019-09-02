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
            ResponseEntity<PersonControllerResponse> response = null;

            try {
                udiStubConsumer.deleteUdiPerson(progress.getBestillingId(), norskIdent.getIdent());

                PersonTo udiPerson = mapperFacade.map(bestilling.getUdistub(), PersonTo.class);
                udiPerson.setIdent(norskIdent.getIdent());
                setPersonDefaultsIfUnspecified(udiPerson);

                response = udiStubConsumer.createUdiPerson(progress.getBestillingId(), udiPerson);
                if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
                    String reason = response.getBody() == null ? "Ukjent årsak" : response.getBody().getReason();
                    throw new UdiStubException(reason);
                }
                appendOkStatus(status, response);

            } catch (UdiStubException e) {
                if (response != null &&
                        response.getBody() != null &&
                        response.getBody().getReason() != null) {
                    appendErrorStatus(status, e, response.getBody().getReason());
                    log.error("Gjenopprett feilet for udistubclient: {}, {}", e.getMessage(), response.getBody().getReason(), e);
                } else {
                    appendErrorStatus(status, e, "ukjent årsak");
                    log.error("Gjenopprett feilet for udistubclient: {}", e.getMessage(), e);
                }
            }
            progress.setUdistubStatus(status.toString());
        }
    }

    private static void appendOkStatus(StringBuilder status, ResponseEntity<PersonControllerResponse> postResponse) {
        if (postResponse != null
                && postResponse.getBody() != null
                && postResponse.getBody().getPerson() != null) {
            status.append("OK: ")
                    .append("ident=")
                    .append(postResponse.getBody().getPerson().getIdent());
        }
    }

    private static void appendErrorStatus(StringBuilder status, RuntimeException e, String reason) {
        status.append("FEIL: ")
                .append(e.getMessage())
                .append(": ")
                .append(reason);

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
