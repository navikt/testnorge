package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdForvalter.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sdForvalter.consumer.rs.KrrConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SamConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SkdConsumer;
import no.nav.registre.sdForvalter.consumer.rs.TpConsumer;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentInitializationService {

    private final AaregRepository aaregRepository;
    private final TpsRepository tpsRepository;
    private final KrrRepository krrRepository;

    private final AaregConsumer aaregConsumer;
    private final SkdConsumer skdConsumer;
    private final KrrConsumer krrConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final TpConsumer tpConsumer;
    private final SamConsumer samConsumer;

    @Value("${tps.statisk.avspillergruppeId}")
    private Long staticDataPlaygroup;

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
        Set<String> fnrs = initializeSkd(environment);
        iniitalizeTp(environment, fnrs);
        initializeSam(environment, fnrs);
        initializeKrr();
        boolean didInitializeAareg = initializeAareg(environment);
        if (!didInitializeAareg) {
            log.warn("Fullførte ikke initialiseringen av miljøet: {} i Aareg", environment);
        }
    }

    /**
     * Metoden oppretter nye meldinger for de som ikke har meldinger fra før av og spiller av disse i gitt miljø
     *
     * @param environment Miljø som de faste meldingene skal spilles av
     * @return Et set med fnrs som ekisterer i gruppen etter at den har blitt oppdatert
     */
    private Set<String> initializeSkd(String environment) {
        Set<TpsModel> tpsSet = new HashSet<>();
        tpsRepository.findAll().forEach(tpsSet::add);

        Set<String> playgroupFnrs = hodejegerenConsumer.getPlaygroupFnrs(staticDataPlaygroup);
        tpsSet = tpsSet.parallelStream().filter(t -> playgroupFnrs.contains(t.getFnr())).collect(Collectors.toSet());

        if (tpsSet.isEmpty()) {
            return new HashSet<>();
        }

        skdConsumer.createTpsMessagesInGroup(tpsSet, staticDataPlaygroup);

        skdConsumer.send(staticDataPlaygroup, environment);

        return hodejegerenConsumer.getPlaygroupFnrs(staticDataPlaygroup);
    }

    /**
     * Metoden legger til identer i gitt miljø med gitte arbeidsforhold definert i databasen.
     *
     * @param environment Miljøet arbeidsforholdene skal legges til i
     */
    private boolean initializeAareg(String environment) {
        Set<AaregModel> aaregSet = new HashSet<>();
        aaregRepository.findAll().forEach(aaregSet::add);
        return aaregConsumer.send(aaregSet, environment);
    }

    /**
     * Metoden legger til dataen i databasen i krr hvis ikke fines fra før av
     */
    private void initializeKrr() {
        Set<KrrModel> dkifSet = new HashSet<>();
        krrRepository.findAll().forEach(dkifSet::add);

        Set<String> existing = krrConsumer.getContactInformation(dkifSet.parallelStream().map(KrrModel::getFnr).collect(Collectors.toSet()));

        dkifSet = dkifSet.parallelStream().filter(t -> existing.contains(t.getFnr())).collect(Collectors.toSet());

        if (dkifSet.isEmpty()) {
            return;
        }
        krrConsumer.send(dkifSet);
    }

    private void iniitalizeTp(String environment, Set<String> fnrs) {
        tpConsumer.send(fnrs, environment);
    }

    private void initializeSam(String environment, Set<String> fnrs) {
        samConsumer.send(fnrs, environment);
    }

}
