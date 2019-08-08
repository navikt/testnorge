package no.nav.dolly.common;

import static java.util.Arrays.asList;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.testrepositories.BrukerTestRepository;
import no.nav.dolly.testrepositories.GruppeTestRepository;
import no.nav.dolly.testrepositories.TeamTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TestdataFactory {

    @Autowired
    private TeamTestRepository teamTestRepository;

    @Autowired
    private BrukerTestRepository brukerTestRepository;

    @Autowired
    private GruppeTestRepository gruppeTestRepository;

    public void clearDatabase() {
        teamTestRepository.deleteAll();
        teamTestRepository.flush();

        brukerTestRepository.deleteAll();
        brukerTestRepository.flush();
    }

    public Bruker createBruker(String navIdent) {
        Bruker bruker = Bruker.builder().navIdent(navIdent).build();
        return brukerTestRepository.save(bruker);
    }

    public Team createTeam(Bruker eier, String name, String desc, Bruker... medlemmer) {
        Team team = Team.builder()
                .navn(name)
                .beskrivelse(desc)
                .datoOpprettet(LocalDate.now())
                .eier(eier)
                .medlemmer(asList(medlemmer)).build();
        return teamTestRepository.save(team);
    }

}
