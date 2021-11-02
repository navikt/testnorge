package no.nav.dolly.bestilling.tpsf;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.tpsf.SendSkdMeldingTilTpsResponse;

@Component
public class TpsfResponseHandler {

    private static final int MAX_LENGTH_VARCHAR2 = 4000;

    public String extractTPSFeedback(List<SendSkdMeldingTilTpsResponse> responses) {
        StringBuilder sb = new StringBuilder(50);

        for (SendSkdMeldingTilTpsResponse response : responses) {
            sb.append("{(personId: ")
                    .append(response.getPersonId())
                    .append("),(meldingstype: ")
                    .append(response.getSkdmeldingstype())
                    .append("),(miljoer: ");
            for (Map.Entry<String, String> entry : response.getStatus().entrySet()) {
                sb.append('<')
                        .append(entry.getKey())
                        .append('=')
                        .append(entry.getValue().trim())
                        .append(">,");
            }

            sb.append(")}");
        }

        return sbToStringForDB(sb);
    }

    public void setErrorMessageToBestillingsProgress(Exception e, BestillingProgress progress) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage());
        if (nonNull(e.getCause())) {
            sb.append("  cause: ").append(e.getCause().getMessage());
        }
        sb.append("  localizedMsg: ").append(e.getLocalizedMessage());

        if (e instanceof HttpClientErrorException) {
            String body = ((HttpClientErrorException) e).getResponseBodyAsString();
            sb.append("   reponseBody: ").append(body);
        }

        progress.setFeil(sbToStringForDB(sb));
    }

    private String sbToStringForDB(StringBuilder sb) {
        return format("%s END", sb.substring(0, sb.length() > MAX_LENGTH_VARCHAR2 ? MAX_LENGTH_VARCHAR2 - 10 : sb.length()));
    }
}