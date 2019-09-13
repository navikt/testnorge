package no.nav.dolly.bestilling.udistub;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.udistub.UdiStubDefaultPersonUtil.setPersonDefaultsIfUnspecified;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;

@Slf4j
@Service
public final class UdiStubClient implements ClientRegister {

    @Autowired
    private UdiStubConsumer udiStubConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getUdistub())) {
            StringBuilder status = new StringBuilder();
            ResponseEntity<UdiPersonControllerResponse> response = null;

            try {
                udiStubConsumer.deleteUdiPerson(norskIdent.getIdent());

                UdiPerson udiPerson = mapperFacade.map(bestilling.getUdistub(), UdiPerson.class);
                udiPerson.setIdent(norskIdent.getIdent());
                setPersonDefaultsIfUnspecified(udiPerson);

                response = udiStubConsumer.createUdiPerson(udiPerson);
                if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
                    String reason = nonNull(response.getBody()) ? response.getBody().getReason() : "Ukjent årsak";
                    throw new UdiStubException(reason);
                }
                appendOkStatus(status, response);

            } catch (UdiStubException e) {

                if (response != null &&
                        response.getBody() != null &&
                        response.getBody().getReason() != null) {
                    appendErrorStatus(status, e, response.getBody().getReason());
                    log.error("Gjenopprett feilet for udistubclient: {}", response.getBody().getReason(), e);
                } else {
                    appendErrorStatus(status, e, "ukjent årsak");
                    log.error("Gjenopprett feilet for udistubclient: {}", e.getMessage(), e);
                }
            }
            progress.setUdistubStatus(status.toString());
        }
    }

    @Override public void release(List<String> identer) {

        identer.forEach(ident -> udiStubConsumer.deleteUdiPerson(ident));
    }

    private static void appendOkStatus(StringBuilder status, ResponseEntity<UdiPersonControllerResponse> postResponse) {
        if (postResponse != null &&
                postResponse.getBody() != null &&
                postResponse.getBody().getReason() != null) {
            status.append("OK: ident=").append(postResponse.getBody().getPerson().getIdent());
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
