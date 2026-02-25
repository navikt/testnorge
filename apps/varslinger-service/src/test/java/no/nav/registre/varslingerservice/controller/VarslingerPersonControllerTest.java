package no.nav.registre.varslingerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.registre.varslingerservice.repository.BrukerRepository;
import no.nav.registre.varslingerservice.repository.MottattVarslingRepository;
import no.nav.registre.varslingerservice.repository.VarslingRepository;
import no.nav.registre.varslingerservice.repository.model.BrukerModel;
import no.nav.registre.varslingerservice.repository.model.MottattVarslingModel;
import no.nav.registre.varslingerservice.repository.model.VarslingModel;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedId;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollySpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class VarslingerPersonControllerTest {

    @MockitoBean
    public GetAuthenticatedToken getAuthenticatedToken;

    @MockitoBean
    public GetAuthenticatedId getAuthenticatedId;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private VarslingRepository varslingRepository;

    @Autowired
    private MottattVarslingRepository mottattVarslingRepository;

    @Autowired
    private BrukerRepository brukerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void afterEach() {
        mottattVarslingRepository.deleteAll();
        brukerRepository.deleteAll();
        varslingRepository.deleteAll();
    }

    @Test
    void testNoWarningsInRepository()
            throws Exception {
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Token.builder().clientCredentials(false).build());
        when(getAuthenticatedId.call())
                .thenReturn(loggedInUser.getObjectId());

        mvc.perform(get("/api/v1/varslinger/person/ids"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void testTwoOfThreeWarningsInRepositoryBelongToLoggedInUser()
            throws Exception {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("varsel1").build());
        var v2 = varslingRepository.save(VarslingModel.builder().varslingId("varsel2").build());
        var v3 = varslingRepository.save(VarslingModel.builder().varslingId("varsel3").build());
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());
        var otherUser = brukerRepository.save(BrukerModel.builder().objectId("bruker2").build());
        mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v1).bruker(loggedInUser).build());
        mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v2).bruker(otherUser).build());
        mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v3).bruker(loggedInUser).build());

        when(getAuthenticatedToken.call())
                .thenReturn(Token.builder().clientCredentials(false).build());
        when(getAuthenticatedId.call())
                .thenReturn(loggedInUser.getObjectId());

        mvc.perform(get("/api/v1/varslinger/person/ids"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.err.println(result.getResponse().getContentAsString()))
                .andExpect(content().json(objectMapper.writeValueAsString(new String[]{v1.getVarslingId(), v3.getVarslingId()})));
    }

    @Test
    void testUpdateWarningDemonstratingNullPointerException()
            throws Exception {
        var v1 = VarslingModel.builder().varslingId("varsel1").build();
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Token.builder().clientCredentials(false).build());
        when(getAuthenticatedId.call())
                .thenReturn(loggedInUser.getObjectId());

        try {
            mvc.perform(put("/api/v1/varslinger/person/ids/{varslingId}", v1.getVarslingId()));
        } catch (ServletException e) {
            assertThat(e.getCause())
                    .satisfies(cause -> assertThat(cause)
                            .isInstanceOf(NullPointerException.class)
                            .hasMessage("Cannot invoke \"no.nav.registre.varslingerservice.domain.Varsling.getVarslingId()\" because \"varsling\" is null"));
        }
    }

    @Test
    void testUpdateWarning()
            throws Exception {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("varsel1").build());
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Token.builder().clientCredentials(false).build());
        when(getAuthenticatedId.call())
                .thenReturn(loggedInUser.getObjectId());

        mvc.perform(put("/api/v1/varslinger/person/ids/{varslingId}", v1.getVarslingId()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/varslinger/person/ids/" + v1.getVarslingId()));
    }

    @Test
    void testGetNonexistingWarning()
            throws Exception {
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Token.builder().clientCredentials(false).build());
        when(getAuthenticatedId.call())
                .thenReturn(loggedInUser.getObjectId());

        mvc.perform(get("/api/v1/varslinger/person/ids/{varslingId}", "someNonExistingId"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetSingleWarning()
            throws Exception {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("varsel1").build());
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());
        mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v1).bruker(loggedInUser).build());

        when(getAuthenticatedToken.call())
                .thenReturn(Token.builder().clientCredentials(false).build());
        when(getAuthenticatedId.call())
                .thenReturn(loggedInUser.getObjectId());

        mvc.perform(get("/api/v1/varslinger/person/ids/{varslingId}", v1.getVarslingId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(v1.getVarslingId()));
    }

    @Test
    void testDeleteNonExistingWarning()
            throws Exception {
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Token.builder().clientCredentials(false).build());
        when(getAuthenticatedId.call())
                .thenReturn(loggedInUser.getObjectId());

        mvc.perform(delete("/api/v1/varslinger/person/ids/{varslingId}", "someNonExistingId"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteWarning()
            throws Exception {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("varsel1").build());
        var v2 = varslingRepository.save(VarslingModel.builder().varslingId("varsel2").build());
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());
        var otherUser = brukerRepository.save(BrukerModel.builder().objectId("bruker2").build());
        var mv1 = mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v1).bruker(loggedInUser).build());
        var mv2 = mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v2).bruker(otherUser).build());

        when(getAuthenticatedToken.call())
                .thenReturn(Token.builder().clientCredentials(false).build());
        when(getAuthenticatedId.call())
                .thenReturn(loggedInUser.getObjectId());

        mvc.perform(delete("/api/v1/varslinger/person/ids/{varslingId}", v1.getVarslingId()))
                .andExpect(status().isOk());

        assertThat(mottattVarslingRepository.findById(mv1.getId()))
                .isEmpty();
        assertThat(mottattVarslingRepository.findById(mv2.getId()))
                .isNotEmpty();
        assertThat(varslingRepository.findById(v1.getVarslingId()))
                .isNotEmpty();
        assertThat(varslingRepository.findById(v2.getVarslingId()))
                .isNotEmpty();

    }

}
