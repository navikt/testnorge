package no.nav.registre.testnorge.person.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.reflections.Reflections.log;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.person.v1.AdresseDTO;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Bostedsadresse;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Data;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Folkeregisteridentifikator;
import no.nav.registre.testnorge.person.consumer.dto.graphql.HentPerson;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Navn;
import no.nav.registre.testnorge.person.consumer.dto.graphql.PdlPerson;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Request;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Vegadresse;
import no.nav.registre.testnorge.test.JsonWiremockHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void should_get_dtoPerson() throws Exception {
        Navn pdlNavn = new Navn("Line", null, "Linesen");
        Folkeregisteridentifikator folkeregisteridentifikator = new Folkeregisteridentifikator("12345678912", null, null);
        Bostedsadresse bostedsadresse = new Bostedsadresse(new Vegadresse("Linegata", "12", "2650", null));
        PdlPerson graphqlResponse = new PdlPerson(Collections.EMPTY_LIST, new Data(new HentPerson(
                Collections.singletonList(pdlNavn),
                Collections.singletonList(bostedsadresse),
                Collections.singletonList(folkeregisteridentifikator))));

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/graphql")
                .withRequestBody(generateGraphqlRequest("12345678912"))
                .withResponseBody(graphqlResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/token/")
                .withResponseBody(Token.builder().access_token("access").expires_in(LocalDateTime.now().plusMinutes(60)).build())
                .stubGet();

        String json = mvc.perform(get("/api/v1/personer/12345678912"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDTO actualPersonDTO = objectMapper.readValue(json, PersonDTO.class);

        PersonDTO expected = PersonDTO.builder()
                .fornavn("Line")
                .etternavn("Linesen")
                .ident("12345678912")
                .adresse(new AdresseDTO("Linegata 12", "2650", null, null))
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