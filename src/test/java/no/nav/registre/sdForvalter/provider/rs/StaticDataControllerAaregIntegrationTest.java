package no.nav.registre.sdForvalter.provider.rs;


import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.domain.Aareg;
import no.nav.registre.sdForvalter.domain.AaregListe;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class StaticDataControllerAaregIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AaregRepository repository;

    @Test
    public void shouldGetAareg() throws Exception {
        AaregModel model = createAaregModel("0101011236", "987654321");
        repository.save(model);
        String json = mvc.perform(get("/api/v1/faste-data/aareg/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AaregListe response = objectMapper.readValue(json, AaregListe.class);
        assertThat(response.getListe()).containsOnly(new Aareg(model));
    }


    public AaregModel createAaregModel(String fnr, String orgnr) {
        AaregModel model = new AaregModel();
        model.setFnr(fnr);
        model.setOrgId(orgnr);
        return model;
    }


    @After
    public void cleanUp() {
        reset();
        repository.deleteAll();
    }

}