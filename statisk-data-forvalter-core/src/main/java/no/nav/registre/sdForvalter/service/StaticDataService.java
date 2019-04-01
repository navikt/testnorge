package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import no.nav.registre.sdForvalter.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.DkifModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.DkifRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticDataService {

    private final AaregRepository aaregRepository;
    private final TpsRepository tpsRepository;
    private final DkifRepository dkifRepository;

    private final HodejegerenConsumer hodejegerenConsumer;

    @Value("${tps.statisk.avspillergruppeId}")
    private Long playgroupStaticData;

    public Set<TpsModel> getLocalTpsDatabaseData() {
        HashSet<TpsModel> tpsModels = new HashSet<>();
        tpsRepository.findAll().forEach(tpsModels::add);
        return tpsModels;
    }

    public Set<AaregModel> getAaregData() {
        HashSet<AaregModel> aaregModels = new HashSet<>();
        aaregRepository.findAll().forEach(aaregModels::add);
        return aaregModels;
    }

    public Set<DkifModel> getDkifData() {
        HashSet<DkifModel> dkifModels = new HashSet<>();
        dkifRepository.findAll().forEach(dkifModels::add);
        return dkifModels;
    }
}
