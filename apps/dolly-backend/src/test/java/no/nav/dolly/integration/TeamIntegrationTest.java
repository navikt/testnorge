package no.nav.dolly.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.config.TestSecurityConfig;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.provider.TeamController;
import no.nav.dolly.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TeamController.class,
        excludeAutoConfiguration = { SecurityAutoConfiguration.class })
@Import(TestSecurityConfig.class)
public class TeamIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private MapperFacade mapperFacade;

    private Team team;
    private RsTeam rsTeam;
    private RsTeamWithBrukere rsTeamWithBrukere;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        var bruker = Bruker.builder()
                .id(1L)
                .brukernavn("user1")
                .build();

        team = Team.builder()
                .id(1L)
                .navn("Test Team")
                .beskrivelse("Test Team Description")
                .brukere(Set.of(bruker))
                .build();

        rsTeam = RsTeam.builder()
                .id(1L)
                .navn("Test Team")
                .beskrivelse("Test Team Description")
                .build();

        rsTeamWithBrukere = new RsTeamWithBrukere();
        rsTeamWithBrukere.setId(1L);
        rsTeamWithBrukere.setNavn("Test Team");
        rsTeamWithBrukere.setBeskrivelse("Test Team Description");
        rsTeamWithBrukere.setBrukerId("user1");
    }

    @Test
    void getAllTeams_shouldReturnListOfTeams() throws Exception {
        when(teamService.fetchAllTeam()).thenReturn(List.of(team));
        when(mapperFacade.mapAsList(List.of(team), RsTeamWithBrukere.class))
                .thenReturn(List.of(rsTeamWithBrukere));

        mockMvc.perform(get("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].navn").value("Test Team"));
    }

    @Test
    void getTeamById_shouldReturnTeam() throws Exception {
        when(teamService.fetchTeamById(1L)).thenReturn(team);
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        mockMvc.perform(get("/api/v1/team/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.navn").value("Test Team"));
    }

    @Test
    void createTeam_shouldReturnCreatedTeam() throws Exception {
        when(mapperFacade.map(any(RsTeam.class), eq(Team.class))).thenReturn(team);
        when(teamService.opprettTeam(team)).thenReturn(team);
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        var result = mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rsTeam)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseContent = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseContent, RsTeamWithBrukere.class);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNavn()).isEqualTo("Test Team");
        assertThat(response.getBeskrivelse()).isEqualTo("Test Team Description");
    }

    @Test
    void updateTeam_shouldReturnUpdatedTeam() throws Exception {
        when(mapperFacade.map(any(RsTeam.class), eq(Team.class))).thenReturn(team);
        when(teamService.updateTeam(eq(1L), any(Team.class))).thenReturn(team);
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        mockMvc.perform(put("/api/v1/team/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rsTeam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.navn").value("Test Team"));
    }

    @Test
    void deleteTeam_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/team/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTeamMember_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/v1/team/1/medlem/user2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void removeTeamMember_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/team/1/medlem/user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}