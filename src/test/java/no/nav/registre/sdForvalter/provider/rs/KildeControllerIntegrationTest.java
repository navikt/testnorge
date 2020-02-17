package no.nav.registre.sdForvalter.provider.rs;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.sdForvalter.database.model.KildeModel;
import no.nav.registre.sdForvalter.database.repository.KildeRepository;
import no.nav.registre.sdForvalter.domain.Kilder;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class KildeControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private KildeRepository kildeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldGetKildeerFromDatabase() throws Exception {
        List<KildeModel> iterable = Arrays.asList(
                new KildeModel("Altinn"),
                new KildeModel("Skatt")
        );
        kildeRepository.saveAll(iterable);

        String body = mvc.perform(MockMvcRequestBuilders.get("/api/v1/kilde/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(new Kilder(iterable)));
    }

    @After
    public void cleanUp() {
        kildeRepository.deleteAll();
    }
}