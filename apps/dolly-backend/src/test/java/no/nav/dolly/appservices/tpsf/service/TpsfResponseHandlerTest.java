package no.nav.dolly.appservices.tpsf.service;

import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.tpsf.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.exceptions.TpsfException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class TpsfResponseHandlerTest {

    private static final String SUCCESS_CODE_TPS = "00";
    private static final String FAIL_CODE_TPS = "08";
    private static final String INNVANDRING_CREATE_NAVN = "InnvandringCreate";

    private SendSkdMeldingTilTpsResponse standarSendSkdResponse = new SendSkdMeldingTilTpsResponse();
    private Map<String, String> status_SuccU1T2_FailQ3 = new HashMap<>();
    private String standardIdent = "ident";
    private BestillingProgress standardProgress = new BestillingProgress();

    private TpsfResponseHandler tpsfResponseHandler = new TpsfResponseHandler();

    @BeforeEach
    public void setup() {
        status_SuccU1T2_FailQ3.put("u1", SUCCESS_CODE_TPS);
        status_SuccU1T2_FailQ3.put("t2", SUCCESS_CODE_TPS);
        status_SuccU1T2_FailQ3.put("q3", FAIL_CODE_TPS);

        standarSendSkdResponse.setSkdmeldingstype(INNVANDRING_CREATE_NAVN);
    }

    @Test
    public void extractFeedbackTPS_viserPersonIdMeldingstypeOgMiljoenesStatukode() {
        standarSendSkdResponse.setStatus(status_SuccU1T2_FailQ3);
        standarSendSkdResponse.setPersonId(standardIdent);

        String feedback = tpsfResponseHandler.extractTPSFeedback(singletonList(standarSendSkdResponse));

        assertThat(feedback.contains("personId: " + standardIdent), is(true));
        assertThat(feedback.contains("meldingstype: " + INNVANDRING_CREATE_NAVN), is(true));
        assertThat(feedback.contains("<u1=00>"), is(true));
        assertThat(feedback.contains("<t2=00>"), is(true));
        assertThat(feedback.contains("<q3=08>"), is(true));
    }

    @Test
    public void handleError_setterFeilPaaBestillingsProgress() {
        HttpClientErrorException e = new HttpClientErrorException(HttpStatus.NOT_FOUND, "statusText");

        tpsfResponseHandler.setErrorMessageToBestillingsProgress(e, standardProgress);

        assertThat(standardProgress.getFeil(), is(notNullValue()));
    }

    @Test
    public void handleError_feilmeldingFraExceptionSettesPaaProgress() {
        TpsfException e = new TpsfException("test-msg");

        tpsfResponseHandler.setErrorMessageToBestillingsProgress(e, standardProgress);

        assertThat(standardProgress.getFeil().contains("test-msg"), is(true));
    }
}