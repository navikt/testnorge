package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.GensakKoder;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.GensakOvKoder;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.registre.arena.core.config.AppConfig;
import no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils;

@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {AapSyntConsumer.class, AppConfig.class, ConsumerUtils.class})
@RestClientTest(AapSyntConsumer.class)
public class AapSyntConsumerTest {

    @Autowired
    private AapSyntConsumer syntConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${synt-arena.rest-api.url}")
    private String serverUrl;

    private int antallMeldinger = 1;

    @Test
    public void shouldSyntetisereAapRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap";
        stubSyntetiserAapRettighet(expectedUri);

        var response = syntConsumer.syntetiserRettighetAap(antallMeldinger);

        NyttVedtakAap rettighet = response.get(0);
        assertThat(rettighet.getAktivitetsfase(), equalTo("UA"));
        assertThat(rettighet.getGenSaksopplysninger().get(0).getKode(), is(GensakKoder.KDATO));
        assertThat(rettighet.getGenSaksopplysninger().get(1).getKode(), is(GensakKoder.BTID));
        assertThat(rettighet.getGenSaksopplysninger().get(2).getKode(), is(GensakKoder.TUUIN));
        assertThat(rettighet.getGenSaksopplysninger().get(3).getKode(), is(GensakKoder.UUFOR));
        assertThat(rettighet.getGenSaksopplysninger().get(4).getKode(), is(GensakKoder.STUBE));
        assertThat(rettighet.getGenSaksopplysninger().get(5).getKode(), is(GensakKoder.OTILF));
        assertThat(rettighet.getGenSaksopplysninger().get(6).getKode(), is(GensakKoder.OTSEK));
        assertThat(rettighet.getGenSaksopplysninger().get(7).getKode(), is(GensakKoder.OOPPL));
        assertThat(rettighet.getGenSaksopplysninger().get(8).getOverordnet(), is(GensakOvKoder.OOPPL));
        assertThat(rettighet.getGenSaksopplysninger().get(8).getKode(), is(GensakKoder.BDSAT));
        assertThat(rettighet.getGenSaksopplysninger().get(9).getKode(), is(GensakKoder.UFTID));
        assertThat(rettighet.getGenSaksopplysninger().get(10).getKode(), is(GensakKoder.BDSRP));
        assertThat(rettighet.getGenSaksopplysninger().get(11).getKode(), is(GensakKoder.GRDRP));
        assertThat(rettighet.getGenSaksopplysninger().get(12).getKode(), is(GensakKoder.GRDTU));
        assertThat(rettighet.getGenSaksopplysninger().get(13).getKode(), is(GensakKoder.BDSTU));
        assertThat(rettighet.getGenSaksopplysninger().get(14).getKode(), is(GensakKoder.GGRAD));
        assertThat(rettighet.getGenSaksopplysninger().get(15).getKode(), is(GensakKoder.ORIGG));
        assertThat(rettighet.getGenSaksopplysninger().get(16).getKode(), is(GensakKoder.YDATO));
        assertThat(rettighet.getGenSaksopplysninger().get(17).getKode(), is(GensakKoder.YINNT));
        assertThat(rettighet.getGenSaksopplysninger().get(18).getKode(), is(GensakKoder.YGRAD));
        assertThat(rettighet.getGenSaksopplysninger().get(19).getKode(), is(GensakKoder.TLONN));
        assertThat(rettighet.getGenSaksopplysninger().get(20).getOverordnet(), is(GensakOvKoder.TLONN));
        assertThat(rettighet.getGenSaksopplysninger().get(20).getKode(), is(GensakKoder.SPROS));
        assertThat(rettighet.getGenSaksopplysninger().get(21).getKode(), is(GensakKoder.NORIN));
        assertThat(rettighet.getGenSaksopplysninger().get(22).getKode(), is(GensakKoder.FAKIN));
        assertThat(rettighet.getGenSaksopplysninger().get(23).getKode(), is(GensakKoder.EOS));
        assertThat(rettighet.getGenSaksopplysninger().get(24).getKode(), is(GensakKoder.LAND));
    }

    @Test
    public void shouldSyntetisereUngUfoerRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap/aaungufor";
        stubSyntetiserUngUfoerRettighet(expectedUri);

        var response = syntConsumer.syntetiserRettighetUngUfoer(antallMeldinger);

        var rettighet = response.get(0);
        var vilkaar = rettighet.getVilkaar();

        assertThat(rettighet.getUtfall(), equalTo("JA"));
        assertThat(rettighet.getVedtaktype(), equalTo("O"));

        assertThat(vilkaar.get(0).getKode(), equalTo("AAUNGNEDS"));
        assertThat(vilkaar.get(0).getStatus(), equalTo("V"));

        assertThat(vilkaar.get(1).getKode(), equalTo("AANEDSSL"));
        assertThat(vilkaar.get(1).getStatus(), equalTo("V"));

        assertThat(vilkaar.get(2).getKode(), equalTo("AASSLDOK"));
        assertThat(vilkaar.get(2).getStatus(), equalTo("V"));

        assertThat(vilkaar.get(3).getKode(), equalTo("AAFOR36"));
        assertThat(vilkaar.get(3).getStatus(), equalTo("V"));
    }

    @Test
    public void shouldSyntetisereTvungenForvaltningRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap/aatfor";
        stubSyntetiserTvungenForvaltningRettighet(expectedUri);

        var response = syntConsumer.syntetiserRettighetTvungenForvaltning(antallMeldinger);

        var rettighet = response.get(0);
        var vilkaar = rettighet.getVilkaar();

        assertThat(rettighet.getUtfall(), equalTo("JA"));
        assertThat(rettighet.getVedtaktype(), equalTo("O"));

        assertThat(vilkaar.get(0).getKode(), equalTo("MISBRUK"));
        assertThat(vilkaar.get(0).getStatus(), equalTo("J"));

        assertThat(vilkaar.get(1).getKode(), equalTo("PSYK"));
        assertThat(vilkaar.get(1).getStatus(), equalTo("V"));

        assertThat(vilkaar.get(2).getKode(), equalTo("SKADE"));
        assertThat(vilkaar.get(2).getStatus(), equalTo("J"));
    }

    @Test
    public void shouldSyntetisereFritakMeldekortRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap/fri_mk";
        stubSyntetiserFritakMeldekortRettighet(expectedUri);

        var response = syntConsumer.syntetiserRettighetFritakMeldekort(antallMeldinger);

        var rettighet = response.get(0);
        var vilkaar = rettighet.getVilkaar();
        assertThat(rettighet.getUtfall(), equalTo("JA"));
        assertThat(rettighet.getVedtaktype(), equalTo("O"));

        assertThat(vilkaar.get(0).getKode(), equalTo("FRI_MAAI"));
        assertThat(vilkaar.get(0).getStatus(), equalTo("J"));

        assertThat(vilkaar.get(1).getKode(), equalTo("FRI_MABH"));
        assertThat(vilkaar.get(1).getStatus(), equalTo("J"));

        assertThat(vilkaar.get(2).getKode(), equalTo("FRI_MAPL"));
        assertThat(vilkaar.get(2).getStatus(), equalTo("J"));

        assertThat(vilkaar.get(3).getKode(), equalTo("FRI_MARY"));
        assertThat(vilkaar.get(3).getStatus(), equalTo("J"));

        assertThat(vilkaar.get(4).getKode(), equalTo("FRI_MATI"));
        assertThat(vilkaar.get(4).getStatus(), equalTo("J"));

        assertThat(vilkaar.get(5).getKode(), equalTo("FRI_MAVI"));
        assertThat(vilkaar.get(5).getStatus(), equalTo("V"));

        assertThat(vilkaar.get(6).getKode(), equalTo("FRI_MAVU"));
        assertThat(vilkaar.get(6).getStatus(), equalTo("J"));
    }

    private void stubSyntetiserAapRettighet(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/aap_synt_response.json")));
    }

    private void stubSyntetiserUngUfoerRettighet(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/ung_ufoer_synt_response.json")));
    }

    private void stubSyntetiserTvungenForvaltningRettighet(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/tvungen_forvaltning_synt_response.json")));
    }

    private void stubSyntetiserFritakMeldekortRettighet(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/fritak_meldekort_synt_response.json")));
    }
}