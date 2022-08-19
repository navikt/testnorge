package no.nav.testnav.apps.tpservice.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class TpSyntConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TpSyntConsumer tpSyntConsumer;

    private Integer numToGen = 3;

    @Test
    public void getYtelser() {
        stubForTpSynt();
        stubToken();
        var ytelser = tpSyntConsumer.getSyntYtelser(numToGen);

        assertThat(ytelser).hasSize(3);
        assertThat(ytelser.get(0).getKYtelseT()).isEqualTo("UKJENT");
        assertThat(ytelser.get(0).getErGyldig()).isEqualTo("0");
        assertThat(ytelser.get(1).getFunkYtelseId()).isEqualTo("10625315");
        assertThat(ytelser.get(0).getVersjon()).isEqualTo("2");
    }

    private void stubForTpSynt() {
        stubFor(get("/synt-tp/api/v1/generate/tp/" + numToGen).willReturn(okJson(
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

    private void stubToken() {
        stubFor(post("/aad/oauth2/v2.0/token").willReturn(okJson(
                "{\"access_token\": \"dummy\"}"
        )));
    }
}