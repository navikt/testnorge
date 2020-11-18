package no.nav.dolly.dbmigrate.mapper;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.oracle.OraBruker;
import no.nav.dolly.domain.jpa.postgres.Bruker;
import no.nav.dolly.domain.jpa.postgres.BrukerFavoritter;
import no.nav.dolly.domain.jpa.postgres.Testgruppe;
import no.nav.dolly.repository.oracle.OraBrukerRepository;
import no.nav.dolly.repository.postgres.BrukerFavoritterRepository;
import no.nav.dolly.repository.postgres.BrukerRepository;
import no.nav.dolly.repository.postgres.TestgruppeRepository;

@Slf4j
@Service
@Order(3)
@RequiredArgsConstructor
public class BrukerFavoritterMigrationService implements MigrationService {

    private final OraBrukerRepository oraBrukerRepository;
    private final BrukerRepository brukerRepository;
    private final TestgruppeRepository testgruppeRepository;
    private final BrukerFavoritterRepository brukerFavoritterRepository;

    @Override
    @Transactional
    public void migrate() {

        Iterable<OraBruker> oraBrukereInput = oraBrukerRepository.findAll(Sort.by("id"));
        Map<String, Bruker> brukere = brukerRepository.findAllByOrderById().stream()
                .collect(Collectors.toMap(MigrationService::getBrukerId, bruker -> bruker));
        Map<String, Testgruppe> testgrupper = testgruppeRepository.findAllByOrderById().stream()
                .collect(Collectors.toMap(Testgruppe::toString, gruppe -> gruppe));

        oraBrukereInput.forEach(bruker ->
                bruker.getFavoritter().forEach(favoritt ->
                        brukerFavoritterRepository.save(BrukerFavoritter.builder()
                                .id(BrukerFavoritter.BrukerFavoritterId.builder()
                                        .brukerId(brukere.get(MigrationService.getBrukerId(bruker)).getId())
                                        .gruppeId(testgrupper.get(favoritt.toString()).getId())
                                        .build())
                                .build())));

        log.info("Migrert brukerfavoritter");
    }
}
