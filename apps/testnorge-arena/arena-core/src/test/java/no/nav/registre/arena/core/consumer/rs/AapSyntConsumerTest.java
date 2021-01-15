package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import no.nav.registre.arena.core.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.GensakKoder;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.GensakOvKoder;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.UTFALL_JA;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.VEDTAK_TYPE_KODE_O;

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AapSyntConsumerTest {

    private AapSyntConsumer syntConsumer;

    private MockWebServer mockWebServer;

    @Value("${synt-arena.rest-api.url}")
    private String serverUrl;

    private List<RettighetSyntRequest> syntRequest;

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.syntConsumer = new AapSyntConsumer(mockWebServer.url("/").toString());

        syntRequest = new ArrayList<>(Collections.singletonList(
                RettighetSyntRequest.builder()
                        .fraDato(LocalDate.now().toString())
                        .tilDato(LocalDate.now().toString())
                        .utfall(UTFALL_JA)
                        .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                        .vedtakDato(LocalDate.now().toString())
                        .build()
        ));
    }

    @Test
    public void shouldSyntetisereAapRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap";
        stubSyntetiserAapRettighet();

        var response = syntConsumer.syntetiserRettighetAap(syntRequest);

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
        stubSyntetiserUngUfoerRettighet();

        var response = syntConsumer.syntetiserRettighetUngUfoer(syntRequest);

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
        stubSyntetiserTvungenForvaltningRettighet();

        var response = syntConsumer.syntetiserRettighetTvungenForvaltning(syntRequest);

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
        stubSyntetiserFritakMeldekortRettighet();

        var response = syntConsumer.syntetiserRettighetFritakMeldekort(syntRequest);

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

    private void stubSyntetiserAapRettighet() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(getResourceFileContent("files/aap/aap_synt_response.json"))
                .setResponseCode(200);

        mockWebServer.enqueue(mockResponse);
    }


    private void stubSyntetiserUngUfoerRettighet() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(getResourceFileContent("files/aap/ung_ufoer_synt_response.json"))
                .setResponseCode(200);

        mockWebServer.enqueue(mockResponse);
    }


    private void stubSyntetiserTvungenForvaltningRettighet() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(getResourceFileContent("files/aap/tvungen_forvaltning_synt_response.json"))
                .setResponseCode(200);

        mockWebServer.enqueue(mockResponse);
    }


    private void stubSyntetiserFritakMeldekortRettighet() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(getResourceFileContent("files/aap/fritak_meldekort_synt_response.json"))
                .setResponseCode(200);

        mockWebServer.enqueue(mockResponse);
    }


    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}