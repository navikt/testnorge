package no.nav.dolly.bestilling.tpsf;

import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;

@Service
public class TpsfResponseHandler {

    private static final int MAX_LENGTH_VARCHAR2 = 4000;

    public String extractTPSFeedback(List<SendSkdMeldingTilTpsResponse> responses) {
        StringBuilder sb = new StringBuilder();

        for (SendSkdMeldingTilTpsResponse response : responses) {
            sb.append("{(personId: ").append(response.getPersonId()).append(")");
            sb.append(",(meldingstype: ").append(response.getSkdmeldingstype()).append(")");
            sb.append(",(miljoer: ");
            Map<String, String> status = response.getStatus();
            for (Map.Entry<String, String> entry : status.entrySet()) {
                sb.append("<").append(entry.getKey()).append("=").append(entry.getValue().trim()).append(">,");
            }

            sb.append(")}");
        }

        return sbToStringForDB(sb);
    }

    public void setErrorMessageToBestillingsProgress(Exception e, BestillingProgress progress) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage());
        if (!isNullOrEmpty(e.getCause())) {
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
        String msg = sb.toString();
        if (msg.length() > 4000) {
            msg = msg.substring(0, (MAX_LENGTH_VARCHAR2 - 10));
            msg = msg + " END";
        }
        return msg;
    }
}
