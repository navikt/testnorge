package no.nav.registre.sdforvalter.provider.rs.v1;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.sdforvalter.JwtDecoderConfig;
import no.nav.registre.sdforvalter.database.model.AaregModel;
import no.nav.registre.sdforvalter.database.repository.AaregRepository;
import no.nav.registre.sdforvalter.domain.Aareg;
import no.nav.registre.sdforvalter.domain.AaregListe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = RANDOM_PORT
)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc()
@AutoConfigureWireMock(port = 0)
@Import(JwtDecoderConfig.class)
class StaticDataControllerV1AaregIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AaregRepository repository;

    public AaregModel createAaregModel(String fnr, String orgnr) {
        AaregModel model = new AaregModel();
        model.setFnr(fnr);
        model.setOrgId(orgnr);
        return model;
    }

    @AfterEach
    public void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldGetAareg() throws Exception {
        AaregModel model = createAaregModel("0101011236", "987654321");
        repository.save(model);
        String json = mvc.perform(get("/api/v1/faste-data/aareg")
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AaregListe response = objectMapper.readValue(json, AaregListe.class);
        assertThat(response.getListe()).containsOnly(new Aareg(model));
    }

}
