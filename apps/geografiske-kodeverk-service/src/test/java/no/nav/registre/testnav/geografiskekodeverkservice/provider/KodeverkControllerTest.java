package no.nav.registre.testnav.geografiskekodeverkservice.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.testnav.geografiskekodeverkservice.domain.Kodeverk;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class KodeverkControllerTest {

    @MockBean
    public JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return_kommuner() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/kommuner"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);
        Assertions.assertFalse(kodeverk.isEmpty());
    }

    @Test
    void should_return_land() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/land"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);
        Assertions.assertFalse(kodeverk.isEmpty());
    }

    @Test
    void should_return_postnummer() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/postnummer"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);
        Assertions.assertFalse(kodeverk.isEmpty());
    }

    @Test
    void should_return_embeter() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/embeter"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);
        Assertions.assertFalse(kodeverk.isEmpty());
    }

    @Test
    void should_return_oslo_kommune_when_kommunenummer_is_from_oslo() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/kommuner")
                        .queryParam("kommunenr", "0301")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);

        Assertions.assertIterableEquals(
                Collections.singletonList(Kodeverk.builder().kode("0301").navn("Oslo").build()),
                kodeverk
        );
    }

    @Test
    void should_return_volda_when_kommunenavn_is_volda() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/kommuner")
                        .queryParam("kommunenavn", "Volda")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);

        Assertions.assertIterableEquals(
                Collections.singletonList(Kodeverk.builder().kode("1577").navn("Volda").build()),
                kodeverk
        );
    }

    @Test
    void should_return_sverige_when_landkode_is_swe() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/land")
                        .queryParam("landkode", "SWE")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);

        Assertions.assertIterableEquals(
                Collections.singletonList(Kodeverk.builder().kode("SWE").navn("SVERIGE").build()),
                kodeverk
        );
    }

    @Test
    void should_return_malaysia_when_land_is_malaysia() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/land")
                        .queryParam("land", "MALAYSIA")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);

        Assertions.assertIterableEquals(
                Collections.singletonList(Kodeverk.builder().kode("MYS").navn("MALAYSIA").build()),
                kodeverk
        );
    }

    @Test
    void should_return_myanmar_when_land_is_myanmar() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/land")
                        .queryParam("land", "MYANMAR (BURMA)")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);

        Assertions.assertIterableEquals(
                Collections.singletonList(Kodeverk.builder().kode("MMR").navn("MYANMAR (BURMA)").build()),
                kodeverk
        );
    }

    @Test
    void should_return_davik_when_poststed_is_davik() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/postnummer")
                        .queryParam("poststed", "DAVIK")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);

        Assertions.assertIterableEquals(
                Collections.singletonList(Kodeverk.builder().kode("6730").navn("DAVIK").build()),
                kodeverk
        );
    }

    @Test
    void should_return_oslo_when_postnummer_is_from_oslo() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/v1/postnummer")
                        .queryParam("postnummer", "0580")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Kodeverk> kodeverk = readResponse(result);

        Assertions.assertIterableEquals(
                Collections.singletonList(Kodeverk.builder().kode("0580").navn("OSLO").build()),
                kodeverk
        );
    }

    @Test
    void should_return_bad_request_when_kommunenummer_is_not_numerical() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/kommuner")
                .queryParam("kommunenr", "Oslo")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_kommunenavn_is_not_alphabetical() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/kommuner")
                .queryParam("kommunenavn", "0301")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_landkode_is_not_three_letters() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/land")
                .queryParam("landkode", "SVERIGE")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_land_is_not_alphabetical() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/land")
                .queryParam("land", "SVER1GE")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_poststed_is_not_aphabetical() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/postnummer")
                .queryParam("poststed", "A0580")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_postnummer_is_not_numerical() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/postnummer")
                .queryParam("postnummer", "OSLO5")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private List<Kodeverk> readResponse(MvcResult result) throws Exception {
        Kodeverk[] array = objectMapper.readValue(result.getResponse().getContentAsString(), Kodeverk[].class);
        return Arrays.stream(array).collect(Collectors.toUnmodifiableList());
    }

}