package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import no.nav.registre.sdForvalter.database.ModelEnum;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;
import no.nav.registre.sdForvalter.util.database.DatabaseInitializer;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticFileService {

    private final AaregRepository aaregRepository;
    private final KrrRepository krrRepository;
    private final TpsRepository tpsRepository;

    @PostConstruct
    public void initializeFromLocalFiles() {
        readAaregLocalFile();
        readDkifLocalFile();
        readTpsLocalFile();
    }

    @SuppressWarnings(value = "unchecked")
    private void readAaregLocalFile() {
        try {
            List<AaregModel> entities = (List<AaregModel>) (List<?>) DatabaseInitializer.initializeFromCsv("statisk_data/aareg.csv", ModelEnum.AAREG, ";");
            aaregRepository.saveAll(entities);
        } catch (IOException e) {
            log.warn("Unable to read local file, expected this to be present when initializing.\nDatabase might not have been initialized with the correct values");
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void readTpsLocalFile() {
        try {
            List<TpsModel> entities = (List<TpsModel>) (List<?>) DatabaseInitializer.initializeFromCsv("statisk_data/tps.csv", ModelEnum.TPS, ";");
            tpsRepository.saveAll(entities);
        } catch (IOException e) {
            log.warn("Unable to read local file, expected this to be present when initializing.\nDatabase might not have been initialized with the correct values");
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void readDkifLocalFile() {
        try {
            List<KrrModel> entities = (List<KrrModel>) (List<?>) DatabaseInitializer.initializeFromCsv("statisk_data/dkif.csv", ModelEnum.DKIF, ";");
            krrRepository.saveAll(entities);
        } catch (IOException e) {
            log.warn("Unable to read local file, expected this to be present when initializing.\nDatabase might not have been initialized with the correct values");
        }
    }

}
