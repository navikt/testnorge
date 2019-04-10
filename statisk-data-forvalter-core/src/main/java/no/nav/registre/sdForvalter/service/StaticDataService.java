package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticDataService {

    private final AaregRepository aaregRepository;
    private final TpsRepository tpsRepository;
    private final KrrRepository krrRepository;

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

    public Set<KrrModel> getDkifData() {
        HashSet<KrrModel> krrModels = new HashSet<>();
        krrRepository.findAll().forEach(krrModels::add);
        return krrModels;
    }

    public Set<TpsModel> saveInTps(Set<TpsModel> data) {
        Set<TpsModel> overlap = new HashSet<>();
        Set<TpsModel> reduced = data.stream().filter(
                t -> {
                    if (tpsRepository.findById(t.getFnr()).isPresent()) {
                        overlap.add(t);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toSet());
        tpsRepository.saveAll(reduced);
        return overlap;
    }

    public Set<AaregModel> saveInAareg(Set<AaregModel> data) {
        Set<AaregModel> overlap = new HashSet<>();
        Set<AaregModel> reduced = data.stream().filter(t -> {
            if (aaregRepository.findById(t.getFnr()).isPresent()) {
                overlap.add(t);
                return false;
            }
            return true;
        }).collect(Collectors.toSet());
        aaregRepository.saveAll(reduced);
        return overlap;
    }

    public Set<KrrModel> saveInKrr(Set<KrrModel> data) {
        Set<KrrModel> overlap = new HashSet<>();
        Set<KrrModel> reduced = data.stream().filter(t -> {
            if (krrRepository.findById(t.getFnr()).isPresent()) {
                overlap.add(t);
                return false;
            }
            return true;
        }).collect(Collectors.toSet());
        krrRepository.saveAll(reduced);
        return overlap;
    }
}
