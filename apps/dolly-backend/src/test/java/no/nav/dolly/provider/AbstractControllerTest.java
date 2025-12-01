package no.nav.dolly.provider;

import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;

@DollySpringBootTest
@ExtendWith(TestDatabaseConfig.class)
public abstract class AbstractControllerTest {

    @Autowired
    private BrukerRepository brukerRepository;
    @Autowired
    private TestgruppeRepository testgruppeRepository;
    @Autowired
    private IdentRepository identRepository;

    Mono<Bruker> createBruker() {

        return brukerRepository.save(
                Bruker
                        .builder()
                        .brukerId(UUID.randomUUID().toString())
                        .brukertype(AZURE)
                        .brukernavn("testbruker")
                        .build());
    }

    Mono<Bruker> saveBruker(Bruker bruker) {
        return brukerRepository.save(bruker);
    }

    Mono<Testgruppe> createTestgruppe(String navn, Bruker bruker) {

        return testgruppeRepository.save(
                Testgruppe
                        .builder()
                        .navn(navn)
                        .hensikt("Testing")
                        .opprettetAvId(bruker.getId())
                        .opprettetAv(bruker)
                        .sistEndretAv(bruker)
                        .sistEndretAvId(bruker.getId())
                        .datoEndret(LocalDate.now())
                        .sistEndretAv(bruker)
                        .build());
    }

    Mono<Testident> createTestident(String ident, Testgruppe testgruppe) {

        return identRepository.save(
                Testident
                        .builder()
                        .ident(ident)
                        .gruppeId(testgruppe.getId())
                        .master(PDL)
                        .build());
    }

    Mono<Testident> findTestident(String ident) {

        return identRepository.findByIdent(ident);
    }
}