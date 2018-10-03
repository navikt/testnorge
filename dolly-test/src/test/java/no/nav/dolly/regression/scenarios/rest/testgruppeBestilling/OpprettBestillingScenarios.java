package no.nav.dolly.regression.scenarios.rest.testgruppeBestilling;

import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.testdata.builder.RsTpsfBestillingBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OpprettBestillingScenarios extends TestgruppeBestillingTestCaseBase {

    @Test
    public void happyPath() throws Exception{
        Long gruppeId = gruppeRepository.findByNavn(standardGruppenavn).getId();
        String url = getEndpointUrl(gruppeId);
        CompletableFuture<String> future = new CompletableFuture<>();

        RsTpsfBestilling tpsfBestilling = RsTpsfBestillingBuilder.builder()
                .antall(1)
                .kjonn("M")
                .foedtEtter(LocalDate.of(2000, 1, 1))
                .build()
                .convertToRealRsTpsfBestilling();

        standardBestilling_u6.setTpsf(tpsfBestilling);

//        MvcResult mvcResult = mvcMock.perform(post(url)
//                .contentType(APPLICATION_JSON_UTF8)
//                .content(convertObjectToJson(standardBestilling_u6)))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        RsBestilling responseBestilling = convertMvcResultToObject(mvcResult, RsBestilling.class);
    }

}
