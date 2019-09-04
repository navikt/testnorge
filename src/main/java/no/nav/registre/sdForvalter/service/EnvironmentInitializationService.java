package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdForvalter.consumer.rs.EregMapperConsumer;
import no.nav.registre.sdForvalter.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sdForvalter.consumer.rs.KrrConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SamConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SkdConsumer;
import no.nav.registre.sdForvalter.consumer.rs.TpConsumer;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentInitializationService {

    private final AaregRepository aaregRepository;
    private final TpsRepository tpsRepository;
    private final KrrRepository krrRepository;
    private final EregRepository eregRepository;

    private final AaregConsumer aaregConsumer;
    private final SkdConsumer skdConsumer;
    private final KrrConsumer krrConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final TpConsumer tpConsumer;
    private final SamConsumer samConsumer;
    private final EregMapperConsumer eregMapperConsumer;

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
        Set<String> missingFnrs = initializeSkd(environment);
        if (missingFnrs.size() != 0) {
            log.warn("Identer som ikke ble opprettet i tps: {}, disse burde sjekkes på nytt etter en liten stund, se hodejegeren", missingFnrs);
        }

        Set<String> fnrs = hodejegerenConsumer.getLivingFnrs(staticDataPlaygroup, environment);
        iniitalizeTp(environment, fnrs);
        initializeSam(environment, fnrs);
        initializeKrr();
        Map<String, String> aaregStatusMap = initializeAareg(environment);
        if (!aaregStatusMap.isEmpty()) {
            log.warn("Fullførte ikke initialiseringen av miljøet: {} i Aareg, feilet på identer {}", environment, aaregStatusMap);
        }
        String flatfileFromEregMapper = initializeEreg(environment);
        if (!"".equals(flatfileFromEregMapper) && flatfileFromEregMapper != null) {
            log.info(flatfileFromEregMapper);
        }
    }

    /**
     * Metoden oppretter nye meldinger for de som ikke har meldinger fra før av og spiller av disse i gitt miljø
     *
     * @param environment Miljø som de faste meldingene skal spilles av
     * @return Et set som inneholder de identene som ikke har blitt opprettet i tps
     */
    public Set<String> initializeSkd(String environment) {
        Set<TpsModel> tpsSet = new HashSet<>();
        tpsRepository.findAll().forEach(tpsSet::add);

        tpsSet = tpsSet.parallelStream().filter(tpsModel -> tpsModel.getVarighet().shouldUse()).collect(Collectors.toSet());

        Set<String> playgroupFnrs = hodejegerenConsumer.getPlaygroupFnrs(staticDataPlaygroup);
        tpsSet = tpsSet.parallelStream().filter(t -> !playgroupFnrs.contains(t.getFnr())).collect(Collectors.toSet());

        if (!tpsSet.isEmpty()) {
            skdConsumer.createTpsMessagesInGroup(tpsSet, staticDataPlaygroup);
        }

        skdConsumer.send(staticDataPlaygroup, environment);

        Set<String> livingFnrs = hodejegerenConsumer.getLivingFnrs(staticDataPlaygroup, environment);

        return playgroupFnrs.parallelStream().filter(t -> !livingFnrs.contains(t)).collect(Collectors.toSet());
    }

    /**
     * Metoden legger til identer i gitt miljø med gitte arbeidsforhold definert i databasen.
     *
     * @param environment Miljøet arbeidsforholdene skal legges til i
     */
    public Map<String, String> initializeAareg(String environment) {
        Set<AaregModel> aaregSet = new HashSet<>();
        aaregRepository.findAll().forEach(aaregSet::add);
        aaregSet = aaregSet.parallelStream().filter(aaregModel -> aaregModel.getVarighet().shouldUse()).collect(Collectors.toSet());

        Set<String> fnrs = aaregConsumer.finnPersonerUtenArbeidsforhold(aaregSet.parallelStream().map(AaregModel::getFnr).collect(Collectors.toSet()), environment);

        Set<AaregModel> models = aaregSet.parallelStream().filter(a -> fnrs.contains(a.getFnr())).collect(Collectors.toSet());

        return aaregConsumer.send(models, environment);
    }

    public String initializeEreg(String environment) {
        List<EregModel> data = new ArrayList<>();
        eregRepository.findAll().forEach(
                e -> {
                    if (e.isExcluded()) {
                        return;
                    }
                    data.add(e);
                }
        );
        return eregMapperConsumer.uploadToEreg(data, environment);
    }

    /**
     * Metoden legger til dataen i databasen i krr hvis ikke de finnes fra før av
     */
    public void initializeKrr() {
        Set<KrrModel> dkifSet = new HashSet<>();
        krrRepository.findAll().forEach(dkifSet::add);
        dkifSet = dkifSet.parallelStream().filter(krrModel -> krrModel.getVarighet().shouldUse()).collect(Collectors.toSet());


        Set<String> existing = krrConsumer.getContactInformation(dkifSet.parallelStream().map(KrrModel::getFnr).collect(Collectors.toSet()));

        dkifSet = dkifSet.parallelStream().filter(t -> !existing.contains(t.getFnr())).collect(Collectors.toSet());

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
