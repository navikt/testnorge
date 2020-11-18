package no.nav.dolly.dbmigrate.mapper;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.oracle.OraTransaksjonMapping;
import no.nav.dolly.domain.jpa.postgres.TransaksjonMapping;
import no.nav.dolly.repository.oracle.OraTransaksjonMappingRepository;
import no.nav.dolly.repository.postgres.TransaksjonMappingRepository;

@Slf4j
@Service
@Order(5)
@RequiredArgsConstructor
public class TransaksjonMappingMigrationService implements MigrationService {

    private final OraTransaksjonMappingRepository oraTransaksjonMappingRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;

    @Override
    @Transactional
    public void migrate() {

        Iterable<OraTransaksjonMapping> transMappingInput = oraTransaksjonMappingRepository.findAllByBestIdIsNullOrderByIdAsc();
        transMappingInput.forEach(transaksjon ->
                transaksjonMappingRepository.save(mapTransaksjon(transaksjon)));

        log.info("Migrert transaksjonMapping del II");
    }

    private static TransaksjonMapping mapTransaksjon(OraTransaksjonMapping transMap) {

        return TransaksjonMapping.builder()
                .datoEndret(transMap.getDatoEndret())
                .ident(transMap.getIdent())
                .miljoe(transMap.getMiljoe())
                .system(transMap.getSystem())
                .transaksjonId(transMap.getTransaksjonId())
                .build();
    }
}
