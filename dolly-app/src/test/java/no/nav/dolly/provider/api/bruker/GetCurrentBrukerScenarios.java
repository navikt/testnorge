//package no.nav.dolly.provider.api.bruker;
//
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
//import org.junit.Test;
//import org.springframework.test.web.servlet.MvcResult;
//
//
//public class GetCurrentBrukerScenarios extends BrukerTestCaseBase {
//
//    @Test
//    public void getBrukerBasertPaaInloggetBrukerFraOIDC() throws Exception {
//        MvcResult mvcResult = mvcMock.perform(get(endpointUrl + "/" + "current"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        RsBruker res = convertMvcResultToObject(mvcResult, RsBruker.class);
//
//        assertThat(res.getNavIdent(), is(STANDARD_NAV_IDENT));
//    }
//}
