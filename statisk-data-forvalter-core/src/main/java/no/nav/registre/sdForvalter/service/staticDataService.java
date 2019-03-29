package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import java.io.IOException;
import java.util.List;

import no.nav.registre.sdForvalter.database.ModelEnum;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.DkifModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.DkifRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;
import no.nav.registre.sdForvalter.util.database.DatabaseInitializer;

@Slf4j
@Service
@RequiredArgsConstructor
public class staticDataService {

    private final AaregRepository aaregRepository;
    private final DkifRepository dkifRepository;
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
            List<AaregModel> entities = (List<AaregModel>)(List<?>)DatabaseInitializer.initializeFromCsv("aareg,csv", ModelEnum.AAREG, ",");
            aaregRepository.saveAll(entities);
        } catch (IOException e) {
            log.warn("Unable to read local file, expected this to be present when initializing.\nDatabase might not have been initialized with the correct values");
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void readTpsLocalFile() {
        try {
            List<TpsModel> entities = (List<TpsModel>)(List<?>)DatabaseInitializer.initializeFromCsv("tps", ModelEnum.AAREG, ",");
            tpsRepository.saveAll(entities);
        } catch (IOException e) {
            log.warn("Unable to read local file, expected this to be present when initializing.\nDatabase might not have been initialized with the correct values");
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void readDkifLocalFile() {
        try {
            List<DkifModel> entities = (List<DkifModel>)(List<?>)DatabaseInitializer.initializeFromCsv("dkif", ModelEnum.AAREG, ",");
            dkifRepository.saveAll(entities);
        } catch (IOException e) {
            log.warn("Unable to read local file, expected this to be present when initializing.\nDatabase might not have been initialized with the correct values");
        }
    }

}
