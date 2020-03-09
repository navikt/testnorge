package no.nav.registre.spion.provider.rs;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

import no.nav.registre.spion.consumer.rs.response.aareg.*;
import no.nav.registre.spion.consumer.rs.response.HodejegerenResponse;
import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.registre.spion.util.JsonTestHelper.stubGet;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//TODO: Update test for kafka use.
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
@AutoConfigureWireMock
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
@ActiveProfiles("test")
public class SyntetiseringControllerIntegrationTest {

    private static final String MILJOE = "x2";
    private static final long AVSPILLERGRUPPEID = 10;
    private static final LocalDate SLUTTDATO = LocalDate.now();
    private static final LocalDate STARTDATO = SLUTTDATO.minusMonths(18);
    private static final String IDENT = "123";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void shouldGetDataFromAaregAndHodejegerenAndCreateVedtak() throws Exception{

        final UrlPathPattern hentIdenterIAaregUrl =
                urlPathMatching("(.*)/v1/ident/avspillergruppe/"+AVSPILLERGRUPPEID+"(.*)");
        final UrlPathPattern hentArbeidsforholdIAaregUrl =
                urlPathMatching("(.*)/v1/ident/"+IDENT+"(.*)");
        final UrlPathPattern hentPersondataTilIdentUrl =
                urlPathMatching("(.*)/v1/persondata(.*)");


        SyntetiserSpionRequest vedtakRequest =
                new SyntetiserSpionRequest(AVSPILLERGRUPPEID, MILJOE,1, STARTDATO , SLUTTDATO, 1);

        List<String> identer = Collections.singletonList(IDENT);

        List<AaregResponse> arbeidsforhold = new ArrayList<>();
        arbeidsforhold.add(new AaregResponse(
                1234,
                "",
                new Arbeidstaker("",IDENT,""),
                new Arbeidsgiver("organisasjon", "org_nr"),
               null,
                "",
                null,
                null,
                false,
                LocalDate.now(),
                LocalDate.now(),
                new Sporingsinformasjon()
                ));

        HodejegerenResponse persondata = new HodejegerenResponse(
                IDENT,
                "FORNAVN",
                "MELLOMNAVN",
                "ETTERNAVN",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "");


        stubGet(hentIdenterIAaregUrl, identer,  objectMapper);
        stubGet(hentArbeidsforholdIAaregUrl, arbeidsforhold, objectMapper);
        stubGet(hentPersondataTilIdentUrl, persondata, objectMapper);

        mvc.perform(post("/api/v1/syntetisering/vedtak")
                .content(objectMapper.writeValueAsString(vedtakRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        verify(getRequestedFor(hentIdenterIAaregUrl));
        verify(getRequestedFor(hentArbeidsforholdIAaregUrl));

    }

    @After
    public void cleanUp(){
        reset();
    }

}