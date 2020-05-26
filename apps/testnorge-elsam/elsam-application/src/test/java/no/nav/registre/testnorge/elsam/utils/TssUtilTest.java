package no.nav.registre.testnorge.elsam.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import no.nav.registre.elsam.domain.Lege;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.TssResponse;

@RunWith(MockitoJUnitRunner.class)
public class TssUtilTest {

    private String fnr1 = "19098723229";
    private String fnr2 = "03126126084";

    @Test
    public void shouldBuildLegeFromTssResponse() throws IOException {
        Map<String, TssResponse> tssResponses = hentTssResponse();

        Lege lege1 = TssUtil.buildLegeFromTssResponse(tssResponses.get(fnr1));
        Lege lege2 = TssUtil.buildLegeFromTssResponse(tssResponses.get(fnr2));

        assertThat(lege1.getFnr(), equalTo(fnr1));
        assertThat(lege1.getFornavn(), equalTo("UNG"));
        assertThat(lege1.getMellomnavn(), isEmptyOrNullString());
        assertThat(lege1.getEtternavn(), equalTo("PRODUKSJON"));
        assertThat(lege1.getHprId(), equalTo("311272976"));

        assertThat(lege2.getFnr(), equalTo(fnr2));
        assertThat(lege2.getFornavn(), equalTo("TYDELIG"));
        assertThat(lege2.getMellomnavn(), equalTo("SKRIVE"));
        assertThat(lege2.getEtternavn(), equalTo("STORSKJERM"));
        assertThat(lege2.getHprId(), equalTo("735917527"));
    }

    private static Map<String, TssResponse> hentTssResponse() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = Resources.getResource("testdata/tss_response.json");
        return objectMapper.readValue(resource, new TypeReference<Map<String, TssResponse>>() {
        });
    }
}