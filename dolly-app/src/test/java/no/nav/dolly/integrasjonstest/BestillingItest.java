package no.nav.dolly.integrasjonstest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

public class BestillingItest extends ITestBase {

    private static final String BESTILLING_URI = "/api/v1/person";

    @Test
    @DisplayName("Oppretter en grunnleggende bestilling")
    void gjenopprettBestillingOk() throws Exception {
        String requestBody = getJsonContentsAsString("opprettPersonRequest-happy.json");


        String testcode = "placeholder";
    }

    private void sendBestiling(String body) {
//        return this.restTemplate.exchange(BESTILLING_URI, HttpMethod.POST, createHttpEntityWithBody(body), PersonControllerResponse.class);
    }

//    private HttpEntity createHttpEntityWithBody(String body) {
////        return new HttpEntity(body, createHeaders());
//    }
}
