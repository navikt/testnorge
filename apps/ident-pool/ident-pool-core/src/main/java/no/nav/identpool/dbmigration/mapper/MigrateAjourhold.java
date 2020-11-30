package no.nav.identpool.dbmigration.mapper;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.oracle.OraAjourhold;
import no.nav.identpool.domain.postgres.Ajourhold;
import no.nav.identpool.repository.oracle.OraAjourholdRepository;
import no.nav.identpool.repository.postgres.AjourholdRepository;

@Slf4j
@Order(2)
@Service
@RequiredArgsConstructor
public class MigrateAjourhold implements MigrationService {

    private static final int PAGE_SIZE = 100;

    private final OraAjourholdRepository oraAjourholdRepository;
    private final AjourholdRepository ajourholdRepository;

    @Override public void migrate() {

        Page<OraAjourhold> ajourhold = oraAjourholdRepository.findAllByOrderByIdentity(PageRequest.of(0, PAGE_SIZE));
        IntStream.range(0, ajourhold.getTotalPages())
                .boxed()
                .parallel()
                .map(pageNo -> {
                    ajourholdRepository.saveAll(
                            oraAjourholdRepository.findAllByOrderByIdentity(PageRequest.of(pageNo, PAGE_SIZE))
                                    .getContent().parallelStream()
                                    .map(MigrateAjourhold::getAjourhold)
                                    .collect(Collectors.toList()));
                    log.info("Migrert page {} of total {} ajourhold", pageNo, ajourhold.getTotalPages());
                    return pageNo;
                })
                .collect(Collectors.toList());

        log.info("Migrert Ajourhold tabell");
    }

    private static Ajourhold getAjourhold(OraAjourhold ajourhold) {

        return Ajourhold.builder()
                .identity(ajourhold.getIdentity())
                .status(ajourhold.getStatus())
                .feilmelding(ajourhold.getFeilmelding())
                .sistOppdatert(ajourhold.getSistOppdatert())
                .build();
    }
}
