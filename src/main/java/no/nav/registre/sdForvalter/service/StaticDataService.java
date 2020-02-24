package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticDataService {

    private final AaregRepository aaregRepository;
    private final KrrRepository krrRepository;
    private final EregRepository eregRepository;

    public Set<AaregModel> getAaregData() {
        return new HashSet<>((Collection<AaregModel>) aaregRepository.findAll());
    }

    public Set<KrrModel> getDkifData() {
        return new HashSet<>((Collection<KrrModel>) krrRepository.findAll());
    }

    public Set<AaregModel> saveInAareg(Set<AaregModel> data) {
        return data.stream()
                .filter(aaregModel -> aaregRepository.findById(aaregModel.getFnr()).isPresent())
                .map(aaregRepository::save)
                .collect(Collectors.toSet());
    }


    public Set<KrrModel> saveInKrr(Set<KrrModel> data) {
        return data.stream().filter(krrModel -> krrRepository.findByFnr(krrModel.getFnr()).isPresent())
                .map(krrRepository::save)
                .collect(Collectors.toSet());
    }

    public List<String> deleteEreg(List<String> data) {
        return data.stream().map(eregRepository::deleteByOrgnr).map(EregModel::getOrgnr).collect(Collectors.toList());
    }
}
