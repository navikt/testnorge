package no.nav.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import no.nav.jpa.Bruker;
import no.nav.jpa.Team;

public class TeamServiceMedlemmerTest extends AbstractTeamServiceTest {
    private String medlem1 = "medlem1";
    private String medlem2 = "medlem2";
    private Set<String> navidenter = new HashSet<>(Arrays.asList(medlem1, medlem2));
//    private Bruker medlem1bruker = new Bruker(medlem1);
//    private Bruker medlem2bruker = new Bruker(medlem2);
    
    private Set<Bruker> nyeBrukere = createBrukere();
    
    private Set<Bruker> createBrukere() {
        Set<Bruker> nyeBrukere = new HashSet<>();
//        nyeBrukere.add(medlem1bruker);
//        nyeBrukere.add(medlem2bruker);
        return nyeBrukere;
    }
    
    @Before
    public void setupMocks() {
//        when(brukerRepository.findBrukerByNavIdent("medlem1")).thenReturn(medlem1bruker);
//        when(brukerRepository.findBrukerByNavIdent("medlem2")).thenReturn(null);
//        when(brukerRepository.save(medlem2bruker)).thenReturn(medlem2bruker);
//
//        when(teamRepository.findTeamById(any())).thenReturn(team);
    }
    
    /**
     * HVIS medlemmer som skal legges til i team ikke eksisterer i DollyDB, SÅ skal disse brukerne opprettes.
     * Teste happypath av tjenesten add medlemmer. Når medlemmer skal legges til i team, så skal alle medlemmer samtidig oppdateres med medlemskap i databasen.
     */
    @Test
    public void shouldaddMedlemmer() {
//        teamService.addMedlemmer(team.getId(), navidenter);
//
//        Team savedTeam = captureTheTeamSavedToRepo();
//        assertTrue("Er medlemmer lagt til?", savedTeam.getMedlemmer().containsAll(nyeBrukere));
    }
    
    @Test
    public void shouldFjernMedlemmer() {
//        team.setMedlemmer(createBrukere());
//        assertTrue("Is saved Team empty?", !team.getMedlemmer().isEmpty());
//        teamService.fjernMedlemmer(team.getId(), navidenter);
//        Team savedTeam = captureTheTeamSavedToRepo();
//        assertTrue("Is saved Team empty?", savedTeam.getMedlemmer() == null || savedTeam.getMedlemmer().isEmpty());
    }
    
//    private Team captureTheTeamSavedToRepo() {
//        ArgumentCaptor<Team> argumentCaptor = ArgumentCaptor.forClass(Team.class);
//        verify(teamRepository).save(argumentCaptor.capture());
//        return argumentCaptor.getValue();
//    }
    
}