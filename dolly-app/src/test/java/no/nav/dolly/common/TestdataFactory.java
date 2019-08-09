package no.nav.dolly.common;

import static java.util.Arrays.asList;

import no.nav.dolly.common.repository.BestillingProgressTestRepository;
import no.nav.dolly.common.repository.BestillingTestRepository;
import no.nav.dolly.common.repository.BrukerTestRepository;
import no.nav.dolly.common.repository.GruppeTestRepository;
import no.nav.dolly.common.repository.IdentTestRepository;
import no.nav.dolly.common.repository.TeamTestRepository;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TestdataFactory {

    @Autowired
    private TeamTestRepository teamTestRepository;

    @Autowired
    private BrukerTestRepository brukerTestRepository;

    @Autowired
    private GruppeTestRepository gruppeTestRepository;

    @Autowired
    private IdentTestRepository identTestRepository;

    @Autowired
    private BestillingProgressTestRepository bestillingProgressTestRepository;

    @Autowired
    private BestillingTestRepository bestillingTestRepository;

    public void clearDatabase() {
        identTestRepository.deleteAll();
        identTestRepository.flush();

        gruppeTestRepository.deleteAll();
        gruppeTestRepository.flush();

        teamTestRepository.deleteAll();
        teamTestRepository.flush();

        brukerTestRepository.deleteAll();
        brukerTestRepository.flush();

        bestillingProgressTestRepository.deleteAll();
        bestillingProgressTestRepository.flush();

        bestillingTestRepository.deleteAll();
        bestillingTestRepository.flush();
    }

    public Bruker createBruker(String navIdent) {
        Bruker bruker = Bruker.builder().navIdent(navIdent).build();
        return brukerTestRepository.save(bruker);
    }

    public Team createTeam(String name) {
        Bruker bruker = createBruker("NAVIDENT");
        return createTeam(bruker, name, "", bruker);
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

    public Testgruppe createTestgruppe(String navn) {
        Bruker creator = createBruker("CREATOR");
        Team team = createTeam(creator, "Team", "", creator);
        return createTestgruppe(navn, creator, team);
    }

    public Testgruppe createTestgruppe(String navn, Bruker opprettetAv, Team team) {
        Testgruppe testgruppe = Testgruppe.builder()
                .navn(navn)
                .opprettetAv(opprettetAv)
                .sistEndretAv(opprettetAv)
                .datoEndret(LocalDate.now())
                .teamtilhoerighet(team)
                .build();
        return gruppeTestRepository.save(testgruppe);
    }

    public Testgruppe addTestidenterToTestgruppe(Testgruppe testgruppe, Testident... testidenter) {
        Optional<Testgruppe> tg = gruppeTestRepository.findById(testgruppe.getId());
        return tg.map(t -> {
            t.setTestidenter(new HashSet<>(asList(testidenter)));
            return gruppeTestRepository.save(t);
        }).orElseThrow(() -> new RuntimeException("Testgruppe ikke funnet"));
    }

    public Testident createTestident(String ident, Testgruppe testgruppe) {
        Testident testident = TestidentBuilder.builder().ident(ident).testgruppe(testgruppe).build().convertToRealTestident();
        return identTestRepository.save(testident);
    }

    public void addToBrukerFavourites(String navIdent, Long testgruppeId) {
        Bruker brukerByNavIdent = brukerTestRepository.findBrukerByNavIdent(navIdent);
        Testgruppe testgruppe = gruppeTestRepository.findById(testgruppeId).get();
        brukerByNavIdent.getFavoritter().add(testgruppe);
        brukerTestRepository.save(brukerByNavIdent);
    }

    public void clearFavourites(String navIdent) {
        Bruker brukerByNavIdent = brukerTestRepository.findBrukerByNavIdent(navIdent);
        brukerByNavIdent.getFavoritter().clear();
        brukerTestRepository.save(brukerByNavIdent);
    }

}
