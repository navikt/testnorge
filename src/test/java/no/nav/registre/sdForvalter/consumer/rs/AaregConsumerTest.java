package no.nav.registre.sdForvalter.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.registre.sdForvalter.consumer.rs.response.AaregResponse;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.Team;
import no.nav.registre.sdForvalter.database.model.Varighet;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:/application-test.properties")
public class AaregConsumerTest {

    private final String environment = "t1";
    private final Team eier = new Team(
            1L, "test@nav.no", "#team_zynt", "synt", "abc", Collections.emptySet(),
            Collections.emptySet(), Collections.emptySet(), Collections.emptySet(),
            Collections.emptySet()
    );

    private final Varighet varighet = new Varighet(
            1L,
            false, Date.valueOf("2019-04-03"), eier,
            Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet()
    );

    @Autowired
    private AaregConsumer aaregConsumer;

    @Test
    public void sendGyldigeTilAdapter() {

        Set<AaregModel> data = new HashSet<>();
        data.add(new AaregModel("123", 345L, eier, varighet));

        stubAareg();

        List<AaregResponse> response = aaregConsumer.send(data, environment);
        assertThat(response.get(0).getStatusPerMiljoe().get(environment), Matchers.equalTo("OK"));
    }

    private void stubAareg() {
        stubFor(post(urlEqualTo("/testnorge-aareg/api/v1/syntetisering/sendTilAareg?fyllUtArbeidsforhold=true"))
                .withRequestBody(equalToJson(
                        "[{"
                                + "\"arbeidsforhold\": {"
                                + "\"arbeidsgiver\": {"
                                + "\"aktoertype\": \"ORG\","
                                + "\"orgnummer\": \"345\""
                                + "},"
                                + "\"arbeidstaker\": {"
                                + "\"aktoertype\": \"PERS\","
                                + "\"identtype\": \"FNR\","
                                + "\"ident\": \"123\""
                                + "}"
                                + "},"
                                + "\"environments\": [\"t1\"]"
                                + "}]"
                ))
                .willReturn(
                        okJson(
                                "[\n"
                                        + "    {\n"
                                        + "        \"statusPerMiljoe\": {\n"
                                        + "            \"" + environment + "\": \"OK\"\n"
                                        + "        }\n"
                                        + "    }\n"
                                        + "]"
                        )
                ));
    }

    @Test
    public void sendNoeFeil() {
        Set<AaregModel> data = new HashSet<>();
        data.add(new AaregModel("123", 0, eier, varighet));

        stubFeilAareg();
        List<AaregResponse> response = aaregConsumer.send(data, environment);
        assertThat(response.get(0).getStatusPerMiljoe().get(environment), Matchers.startsWith("Feil, OpprettArbeidsforholdUgyldigInput"));
    }

    private void stubFeilAareg() {
        stubFor(post(urlEqualTo("/testnorge-aareg/api/v1/syntetisering/sendTilAareg?fyllUtArbeidsforhold=true"))
                .withRequestBody(equalToJson(
                        "[{"
                                + "\"arbeidsforhold\": {"
                                + "\"arbeidsgiver\": {"
                                + "\"aktoertype\": \"ORG\","
                                + "\"orgnummer\": \"0\""
                                + "},"
                                + "\"arbeidstaker\": {"
                                + "\"aktoertype\": \"PERS\","
                                + "\"identtype\": \"FNR\","
                                + "\"ident\": \"123\""
                                + "}"
                                + "},"
                                + "\"environments\": [\"t1\"]"
                                + "}]"
                ))
                .willReturn(
                        okJson(
                                "[\n"
                                        + "    {\n"
                                        + "        \"statusPerMiljoe\": {\n"
                                        + "            \"" + environment
                                        + "\": \"Feil, OpprettArbeidsforholdUgyldigInput -> Ugyldig input (ForretningsmessigUnntak: feilårsak: BA160 123 BAP0205, feilkilde: Aareg.core, feilmelding: BA160 123 BAP0205\"\n"
                                        + "        }\n"
                                        + "    }\n"
                                        + "]"
                        )
                ));
    }
}