package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import no.nav.registre.sdForvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdForvalter.consumer.rs.DkifConsumer;
import no.nav.registre.sdForvalter.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SamConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SkdConsumer;
import no.nav.registre.sdForvalter.consumer.rs.TpConsumer;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.DkifRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentInitializationService {

    private final AaregRepository aaregRepository;
    private final TpsRepository tpsRepository;
    private final DkifRepository dkifRepository;

    private final AaregConsumer aaregConsumer;
    private final SkdConsumer skdConsumer;
    private final DkifConsumer dkifConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final TpConsumer tpConsumer;
    private final SamConsumer samConsumer;

    @Value("${tps.statisk.avspillergruppeId}")
    private Long playgroupStaticData;

    /**
     * Initialiser et helt miljø med de faste statiske dataene
     *
     * @param environment Miljøet som skal initialiseres
     */
    public void initializeEnvironmentWithStaticData(String environment) {
        /*
          Order of method calls are important to ensure that the values exist in the databases.
          Some methods may fail if at the very least TPS (SKD) have not been created.
          TP and SAM are also critical databases for the fag applications
        */
        initializeSkd(environment);
        iniitalizeTp(environment);
        //TODO: Add SAM
        //initializeSam(environment);
        initializeDkif(environment);
        initializeAareg(environment);
    }

    private void initializeSkd(String environment) {
        Set<Object> tpsSet = new HashSet<>();
        tpsRepository.findAll().forEach(tpsSet::add);


        Set<String> playgroupFnrs = hodejegerenConsumer.getPlaygroupFnrs(playgroupStaticData);
        tpsSet.removeAll(playgroupFnrs);

        skdConsumer.createTpsMessagesInGroup(tpsSet, playgroupStaticData);

        skdConsumer.send(Collections.singleton(playgroupStaticData), environment);
    }

    private void initializeAareg(String environment) {
        Set<Object> aaregSet = new HashSet<>();
        aaregRepository.findAll().forEach(aaregSet::add);
        aaregConsumer.send(aaregSet, environment);
    }

    private void initializeDkif(String environment) {
        Set<Object> dkifSet = new HashSet<>();
        dkifRepository.findAll().forEach(dkifSet::add);
        dkifConsumer.send(dkifSet, environment);
    }

    private void iniitalizeTp(String environment) {
        Set<Object> fnrs = tpConsumer.findFnrs(environment);
        tpConsumer.send(fnrs, environment);
    }

    private void initializeSam(String environment) {
        Set<Object> fnrs = samConsumer.findFnrs(environment);
        samConsumer.send(fnrs, environment);
    }

}
