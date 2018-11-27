package no.nav.dolly.regression.scenarios.rest.testgruppeBestilling;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;

import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.testdata.builder.RsTpsfBestillingBuilder;

public class OpprettBestillingScenarios extends TestgruppeBestillingTestCaseBase {

    @Test
    public void happyPath() {
        Long gruppeId = gruppeRepository.findByNavn(standardGruppenavn).getId();
        String url = getEndpointUrl(gruppeId);
        CompletableFuture<String> future = new CompletableFuture<>();

        RsTpsfBestilling tpsfBestilling = RsTpsfBestillingBuilder.builder()
                .antall(1)
                .kjonn("M")
                .foedtEtter(LocalDate.of(2000, 1, 1).atStartOfDay())
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
