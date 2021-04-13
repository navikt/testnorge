package no.nav.registre.testnorge.arena.consumer.rs;

import static org.assertj.core.api.Assertions.assertThat;

import no.nav.registre.testnorge.arena.consumer.rs.request.synt.SyntRequest;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.UTFALL_JA;
import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.VEDTAK_TYPE_KODE_O;
import static no.nav.registre.testnorge.arena.testutils.ResourceUtils.getResourceFileContent;

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AapSyntConsumerTest {

    private AapSyntConsumer syntConsumer;

    private MockWebServer mockWebServer;

    @Value("${synt-arena.rest-api.url}")
    private String serverUrl;

    private List<SyntRequest> syntRequest;

    private List<GensakKoder> expectedGensakKoder;

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.syntConsumer = new AapSyntConsumer(mockWebServer.url("/").toString());

        syntRequest = new ArrayList<>(Collections.singletonList(
                SyntRequest.builder()
                        .fraDato(LocalDate.now().toString())
                        .tilDato(LocalDate.now().toString())
                        .utfall(UTFALL_JA)
                        .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                        .vedtakDato(LocalDate.now().toString())
                        .build()
        ));

        this.expectedGensakKoder = Arrays.asList(
                GensakKoder.KDATO,
                GensakKoder.BTID,
                GensakKoder.TUUIN,
                GensakKoder.UUFOR,
                GensakKoder.STUBE,
                GensakKoder.OTILF,
                GensakKoder.OTSEK,
                GensakKoder.OOPPL,
                GensakKoder.BDSAT,
                GensakKoder.UFTID,
                GensakKoder.BDSRP,
                GensakKoder.GRDRP,
                GensakKoder.GRDTU,
                GensakKoder.BDSTU,
                GensakKoder.GGRAD,
                GensakKoder.ORIGG,
                GensakKoder.YDATO,
                GensakKoder.YINNT,
                GensakKoder.YGRAD,
                GensakKoder.TLONN,
                GensakKoder.SPROS,
                GensakKoder.NORIN,
                GensakKoder.FAKIN,
                GensakKoder.EOS,
                GensakKoder.LAND
        );
    }

    @Test
    public void shouldSyntetisereAapRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap";
        stubSyntetiserAapRettighet();

        var response = syntConsumer.syntetiserRettighetAap(syntRequest);

        NyttVedtakAap rettighet = response.get(0);
        assertThat(rettighet.getAktivitetsfase()).isEqualTo("UA");
        assertThat(genSaksopplysningKoderMatches(rettighet, expectedGensakKoder)).isTrue();
        assertThat(rettighet.getGenSaksopplysninger().get(8).getOverordnet()).isEqualTo(GensakOvKoder.OOPPL);
        assertThat(rettighet.getGenSaksopplysninger().get(20).getOverordnet()).isEqualTo(GensakOvKoder.TLONN);
    }

    public boolean genSaksopplysningKoderMatches(NyttVedtakAap rettighet, List<GensakKoder> koder){
        for (int i = 0; i < koder.size(); i++){
            if (!rettighet.getGenSaksopplysninger().get(i).getKode().equals(koder.get(i))){
                return false;
            }
        }
        return true;
    }

    @Test
    public void shouldSyntetisereUngUfoerRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap/aaungufor";
        stubSyntetiserUngUfoerRettighet();

        var response = syntConsumer.syntetiserRettighetUngUfoer(syntRequest);

        var rettighet = response.get(0);
        var vilkaar = rettighet.getVilkaar();

        assertThat(rettighet.getUtfall()).isEqualTo("JA");
        assertThat(rettighet.getVedtaktype()).isEqualTo("O");

        assertThat(vilkaar.get(0).getKode()).isEqualTo("AAUNGNEDS");
        assertThat(vilkaar.get(0).getStatus()).isEqualTo("V");

        assertThat(vilkaar.get(1).getKode()).isEqualTo("AANEDSSL");
        assertThat(vilkaar.get(1).getStatus()).isEqualTo("V");

        assertThat(vilkaar.get(2).getKode()).isEqualTo("AASSLDOK");
        assertThat(vilkaar.get(2).getStatus()).isEqualTo("V");

        assertThat(vilkaar.get(3).getKode()).isEqualTo("AAFOR36");
        assertThat(vilkaar.get(3).getStatus()).isEqualTo("V");
    }

    @Test
    public void shouldSyntetisereTvungenForvaltningRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap/aatfor";
        stubSyntetiserTvungenForvaltningRettighet();

        var response = syntConsumer.syntetiserRettighetTvungenForvaltning(syntRequest);

        var rettighet = response.get(0);
        var vilkaar = rettighet.getVilkaar();

        assertThat(rettighet.getUtfall()).isEqualTo("JA");
        assertThat(rettighet.getVedtaktype()).isEqualTo("O");

        assertThat(vilkaar.get(0).getKode()).isEqualTo("MISBRUK");
        assertThat(vilkaar.get(0).getStatus()).isEqualTo("J");

        assertThat(vilkaar.get(1).getKode()).isEqualTo("PSYK");
        assertThat(vilkaar.get(1).getStatus()).isEqualTo("V");

        assertThat(vilkaar.get(2).getKode()).isEqualTo("SKADE");
        assertThat(vilkaar.get(2).getStatus()).isEqualTo("J");
    }

    @Test
    public void shouldSyntetisereFritakMeldekortRettighet() {
        var expectedUri = serverUrl + "/v1/arena/aap/fri_mk";
        stubSyntetiserFritakMeldekortRettighet();

        var response = syntConsumer.syntetiserRettighetFritakMeldekort(syntRequest);

        var rettighet = response.get(0);
        var vilkaar = rettighet.getVilkaar();
        assertThat(rettighet.getUtfall()).isEqualTo("JA");
        assertThat(rettighet.getVedtaktype()).isEqualTo("O");

        assertThat(vilkaar.get(0).getKode()).isEqualTo("FRI_MAAI");
        assertThat(vilkaar.get(0).getStatus()).isEqualTo("J");

        assertThat(vilkaar.get(1).getKode()).isEqualTo("FRI_MABH");
        assertThat(vilkaar.get(1).getStatus()).isEqualTo("J");

        assertThat(vilkaar.get(2).getKode()).isEqualTo("FRI_MAPL");
        assertThat(vilkaar.get(2).getStatus()).isEqualTo("J");

        assertThat(vilkaar.get(3).getKode()).isEqualTo("FRI_MARY");
        assertThat(vilkaar.get(3).getStatus()).isEqualTo("J");

        assertThat(vilkaar.get(4).getKode()).isEqualTo("FRI_MATI");
        assertThat(vilkaar.get(4).getStatus()).isEqualTo("J");

        assertThat(vilkaar.get(5).getKode()).isEqualTo("FRI_MAVI");
        assertThat(vilkaar.get(5).getStatus()).isEqualTo("V");

        assertThat(vilkaar.get(6).getKode()).isEqualTo("FRI_MAVU");
        assertThat(vilkaar.get(6).getStatus()).isEqualTo("J");
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