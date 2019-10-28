package no.nav.dolly.common;

import static java.util.Arrays.asList;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import no.nav.dolly.common.repository.BestillingProgressTestRepository;
import no.nav.dolly.common.repository.BestillingTestRepository;
import no.nav.dolly.common.repository.BrukerTestRepository;
import no.nav.dolly.common.repository.GruppeTestRepository;
import no.nav.dolly.common.repository.IdentTestRepository;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TestdataFactory {

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

        bestillingTestRepository.deleteAll();
        bestillingTestRepository.flush();

        gruppeTestRepository.deleteAll();
        gruppeTestRepository.flush();

        brukerTestRepository.deleteAll();
        brukerTestRepository.flush();

        bestillingProgressTestRepository.deleteAll();
        bestillingProgressTestRepository.flush();
    }

    public Bruker createBruker(String navIdent) {
        Bruker bruker = Bruker.builder().brukerId(navIdent).build();
        return brukerTestRepository.save(bruker);
    }

    public Testgruppe createTestgruppe(String navn) {
        Bruker creator = createBruker("CREATOR");
        return createTestgruppe(navn, creator);
    }

    public Testgruppe createTestgruppe(String navn, Bruker opprettetAv) {
        Testgruppe testgruppe = Testgruppe.builder()
                .navn(navn)
                .opprettetAv(opprettetAv)
                .sistEndretAv(opprettetAv)
                .datoEndret(LocalDate.now())
                .build();
        Testgruppe saved = gruppeTestRepository.save(testgruppe);
        saved.setTestidenter(buildTestIdenter(saved));
        return saved;
    }

    private Set<Testident> buildTestIdenter(Testgruppe testgruppe) {
        return new HashSet<>(asList(
                identTestRepository.save(Testident.builder().ident("123").testgruppe(testgruppe).build()),
                identTestRepository.save(Testident.builder().ident("234").testgruppe(testgruppe).build()),
                identTestRepository.save(Testident.builder().ident("345").testgruppe(testgruppe).build())
        ));
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
        Bruker brukerByNavIdent = brukerTestRepository.findBrukerByBrukerId(navIdent);
        Testgruppe testgruppe = gruppeTestRepository.findById(testgruppeId).get();
        brukerByNavIdent.getFavoritter().add(testgruppe);
        brukerTestRepository.save(brukerByNavIdent);
    }

    public void clearFavourites(String navIdent) {
        Bruker brukerByNavIdent = brukerTestRepository.findBrukerByBrukerId(navIdent);
        brukerByNavIdent.getFavoritter().clear();
        brukerTestRepository.save(brukerByNavIdent);
    }
}
