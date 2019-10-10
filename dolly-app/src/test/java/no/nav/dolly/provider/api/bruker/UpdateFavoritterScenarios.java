//package no.nav.dolly.provider.api.bruker;
//
//import static java.util.Collections.singletonList;
//import static org.assertj.core.util.Sets.newHashSet;
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
//import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import no.nav.dolly.domain.jpa.Bruker;
//import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
//import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
//import org.junit.Test;
//import org.springframework.test.web.servlet.MvcResult;
//
//public class UpdateFavoritterScenarios extends BrukerTestCaseBase {
//
//    @Test
//    public void leggTilFavorittTilBruker() throws Exception {
//        RsBrukerUpdateFavoritterReq request = new RsBrukerUpdateFavoritterReq();
//        request.setGruppeId(standardTestgruppe.getId());
//
//        MvcResult mvcResult = mvcMock.perform(put(endpointUrl + "/" + "leggTilFavoritt")
//                .contentType(APPLICATION_JSON_UTF8)
//                .content(convertObjectToJson(request)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        RsBruker responseBruker = convertMvcResultToObject(mvcResult, RsBruker.class);
//
//        assertThat(responseBruker.getNavIdent(), is(STANDARD_NAV_IDENT));
//        assertThat(responseBruker.getTeams().size(), is(1));
//        assertThat(responseBruker.getFavoritter().size(), is(1));
//    }
//
//    @Test
//    public void fjernEnFavorittFraBruker() throws Exception {
//        Bruker foerAddBruker = brukerTestRepository.findBrukerByNavIdent(STANDARD_NAV_IDENT);
//        foerAddBruker.setFavoritter(newHashSet(singletonList(standardTestgruppe)));
//        Bruker foerFjernBruker = brukerTestRepository.save(foerAddBruker);
//        assertThat(foerFjernBruker.getFavoritter().size(), is(1));
//
//        RsBrukerUpdateFavoritterReq request = new RsBrukerUpdateFavoritterReq();
//        request.setGruppeId(standardTestgruppe.getId());
//
//        MvcResult mvcResult = mvcMock.perform(put(endpointUrl + "/" + "fjernFavoritt")
//                .contentType(APPLICATION_JSON_UTF8)
//                .content(convertObjectToJson(request)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        RsBruker res = convertMvcResultToObject(mvcResult, RsBruker.class);
//
//        assertThat(res.getNavIdent(), is(STANDARD_NAV_IDENT));
//        assertThat(res.getTeams().size(), is(1));
//        assertThat(res.getFavoritter().size(), is(0));
//    }
//}
