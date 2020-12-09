package no.nav.registre.testnorge.person.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.person.v1.AdresseDTO;
import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.Bostedsadresse;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.Data;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.Foedsel;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.Folkeregisteridentifikator;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.HentPerson;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.Navn;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.PdlPerson;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.Request;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.Vegadresse;
import no.nav.registre.testnorge.person.consumer.dto.tpsf.Boadresse;
import no.nav.registre.testnorge.person.consumer.dto.tpsf.IdentMiljoeRequest;
import no.nav.registre.testnorge.person.consumer.dto.tpsf.PersonMiljoeResponse;
import no.nav.registre.testnorge.person.consumer.dto.tpsf.TpsPerson;
import no.nav.registre.testnorge.libs.test.JsonWiremockHelper;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
@AutoConfigureMockMvc(addFilters = false)
public class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void should_get_dtoPerson_from_tps() throws Exception {
        IdentMiljoeRequest tpsfRequest = new IdentMiljoeRequest("12345678921", Collections.singletonList("t4"));

        List<Boadresse> boadresse = Collections.singletonList(Boadresse.builder()
                .gateadresse("Linegata")
                .husnummer("12")
                .postnr("2650")
                .build()
        );
        TpsPerson tpsPerson = TpsPerson.builder()
                .ident("12345678921")
                .fornavn("Line")
                .etternavn("Linesen")
                .foedselsdato(LocalDateTime.parse("1980-10-02T00:00:00"))
                .boadresse(boadresse)
                .build();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/import")
                .withRequestBody(tpsfRequest)
                .withResponseBody(Collections.singletonList(new PersonMiljoeResponse("t4", tpsPerson)))
                .stubPost();

        String json = mvc.perform(get("/api/v1/personer/12345678921")
                .header("persondatasystem", "TPS")
                .header("miljoe", "t4")
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDTO actualPerson = objectMapper.readValue(json, PersonDTO.class);

        PersonDTO expected = PersonDTO.builder()
                .fornavn("Line")
                .etternavn("Linesen")
                .ident("12345678921")
                .foedselsdato(LocalDate.parse("1980-10-02"))
                .adresse(new AdresseDTO("Linegata 12", "2650", null, null))
                .build();

        assertThat(actualPerson).isEqualTo(expected);
    }

    @Test
    public void should_return_404_when_empty_response_from_tpsf() throws Exception {
        IdentMiljoeRequest tpsfRequest = new IdentMiljoeRequest("12345678921", Collections.singletonList("t4"));
        String[] response = {};

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/import")
                .withRequestBody(tpsfRequest)
                .withResponseBody(response)
                .stubPost();

        mvc.perform(get("/api/v1/personer/12345678921")
                .header("persondatasystem", "TPS")
                .header("miljoe", "t4")
        )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void should_get_dtoPerson_from_pdl() throws Exception {
        Navn pdlNavn = new Navn("Line", null, "Linesen");
        Foedsel foedsel = new Foedsel(LocalDate.parse("1980-10-02"));
        Folkeregisteridentifikator folkeregisteridentifikator = new Folkeregisteridentifikator("12345678912", null, null);
        Bostedsadresse bostedsadresse = new Bostedsadresse(new Vegadresse("Linegata", "12", "2650", null));
        PdlPerson graphqlResponse = new PdlPerson(Collections.EMPTY_LIST,
                new Data(new HentPerson(
                        Collections.singletonList(pdlNavn),
                        Collections.singletonList(foedsel),
                        Collections.singletonList(bostedsadresse),
                        Collections.singletonList(folkeregisteridentifikator))));

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/tags")
                .withResponseBody(Collections.emptySet())
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/graphql")
                .withRequestBody(generateGraphqlRequest("12345678912"))
                .withResponseBody(graphqlResponse)
                .stubPost();


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/token/")
                .withResponseBody(Token.builder()
                        .access_token("access")
                        .expires_in(LocalDateTime.now().plusMinutes(60))
                        .build())
                .stubGet();

        String json = mvc.perform(get("/api/v1/personer/12345678912")
                .header("persondatasystem", "PDL")
                .header("miljoe", "")
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDTO actualPersonDTO = objectMapper.readValue(json, PersonDTO.class);

        PersonDTO expected = PersonDTO.builder()
                .fornavn("Line")
                .etternavn("Linesen")
                .ident("12345678912")
                .foedselsdato(LocalDate.parse("1980-10-02"))
                .adresse(new AdresseDTO("Linegata 12", "2650", null, null))
                .tags(Collections.emptySet())
                .build();

        assertThat(actualPersonDTO).isEqualTo(expected);
    }

    Request generateGraphqlRequest(String ident) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("ident", ident);
        variables.put("historikk", true);

        String query = null;
        InputStream queryStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("pdl/pdlQuery.graphql");
        try {
            query = new BufferedReader(new InputStreamReader(queryStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Lesing av queryressurs feilet");
        }

        return Request.builder()
                .query(query)
                .variables(variables)
                .build();
    }

    @AfterEach
    public void cleanUp() {
        WireMock.reset();
    }
}