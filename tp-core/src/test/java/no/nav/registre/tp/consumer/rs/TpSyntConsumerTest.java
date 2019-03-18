package no.nav.registre.tp.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import no.nav.registre.tp.database.models.TYtelse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class TpSyntConsumerTest {

    @Autowired
    private TpSyntConsumer tpSyntConsumer;

    private Integer numToGen = 3;

    @Test
    public void getYtelser() {
        stubForTpSynt();
        List<TYtelse> ytelser = tpSyntConsumer.getYtelser(numToGen);
        assertEquals(3, ytelser.size());
        assertEquals("UKJENT", ytelser.get(0).getK_Ytelse_T());
        assertEquals("0", ytelser.get(0).getErGyldig());
        assertEquals("10625315", ytelser.get(1).getFunkYtelseId());
        assertEquals("2", ytelser.get(0).getVersjon());
    }

    private void stubForTpSynt() {
        stubFor(get("/tpSynt/api/v1/generate/tp?numToGenerate=" + numToGen).willReturn(okJson(
                "[\n"
                        + "    {\n"
                        + "        \"dato_bruk_fom\": \"2011-09-24\",\n"
                        + "        \"dato_bruk_tom\": \"2011-09-24\",\n"
                        + "        \"dato_endret\": \"2011-09-24\",\n"
                        + "        \"dato_innm_ytel_fom\": \"\",\n"
                        + "        \"dato_opprettet\": \"2008-12-02\",\n"
                        + "        \"dato_ytel_iver_fom\": \"2001-10-01\",\n"
                        + "        \"dato_ytel_iver_tom\": \"\",\n"
                        + "        \"endret_av\": \"\",\n"
                        + "        \"er_gyldig\": \"0\",\n"
                        + "        \"funk_ytelse_id\": \"10637556\",\n"
                        + "        \"k_melding_t\": \"\",\n"
                        + "        \"k_ytelse_t\": \"UKJENT\",\n"
                        + "        \"opprettet_av\": \"\",\n"
                        + "        \"versjon\": \"2\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"dato_bruk_fom\": \"2011-09-24\",\n"
                        + "        \"dato_bruk_tom\": \"2011-09-24\",\n"
                        + "        \"dato_endret\": \"2011-09-24\",\n"
                        + "        \"dato_innm_ytel_fom\": \"\",\n"
                        + "        \"dato_opprettet\": \"2008-12-02\",\n"
                        + "        \"dato_ytel_iver_fom\": \"2003-10-01\",\n"
                        + "        \"dato_ytel_iver_tom\": \"\",\n"
                        + "        \"endret_av\": \"\",\n"
                        + "        \"er_gyldig\": \"0\",\n"
                        + "        \"funk_ytelse_id\": \"10625315\",\n"
                        + "        \"k_melding_t\": \"\",\n"
                        + "        \"k_ytelse_t\": \"UKJENT\",\n"
                        + "        \"opprettet_av\": \"\",\n"
                        + "        \"versjon\": \"1\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"dato_bruk_fom\": \"2011-09-24\",\n"
                        + "        \"dato_bruk_tom\": \"2011-09-24\",\n"
                        + "        \"dato_endret\": \"2011-09-24\",\n"
                        + "        \"dato_innm_ytel_fom\": \"\",\n"
                        + "        \"dato_opprettet\": \"2008-12-02\",\n"
                        + "        \"dato_ytel_iver_fom\": \"1994-03-01\",\n"
                        + "        \"dato_ytel_iver_tom\": \"\",\n"
                        + "        \"endret_av\": \"\",\n"
                        + "        \"er_gyldig\": \"0\",\n"
                        + "        \"funk_ytelse_id\": \"10743636\",\n"
                        + "        \"k_melding_t\": \"\",\n"
                        + "        \"k_ytelse_t\": \"UKJENT\",\n"
                        + "        \"opprettet_av\": \"\",\n"
                        + "        \"versjon\": \"2\"\n"
                        + "    }\n"
                        + "]"
        )));
    }
}