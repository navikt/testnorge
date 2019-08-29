package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.Team;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;
import no.nav.registre.sdForvalter.database.repository.TeamRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;
import no.nav.registre.sdForvalter.provider.rs.request.AaregRequest;
import no.nav.registre.sdForvalter.provider.rs.request.TpsRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticDataService {

    private final AaregRepository aaregRepository;
    private final TpsRepository tpsRepository;
    private final KrrRepository krrRepository;
    private final EregRepository eregRepository;
    private final TeamRepository teamRepository;

    @Value("${tps.statisk.avspillergruppeId}")
    private Long playgroupStaticData;

    public Set<Team> getAllTeams() {
        return new HashSet<>((Collection<? extends Team>) teamRepository.findAll());
    }

    public Team saveTeam(Team team) {
        return teamRepository.findByNavn(team.getNavn()).orElseGet(() -> teamRepository.save(team));
    }

    public Set<TpsModel> getLocalTpsDatabaseData() {
        return new HashSet<>((Collection<? extends TpsModel>) tpsRepository.findAll());
    }

    public Set<AaregModel> getAaregData() {
        return new HashSet<>((Collection<? extends AaregModel>) aaregRepository.findAll());
    }

    public Set<KrrModel> getDkifData() {
        return new HashSet<>((Collection<? extends KrrModel>) krrRepository.findAll());
    }

    public Set<TpsModel> saveInTps(Set<TpsRequest> data) {
        Set<TpsModel> reduced = data.stream().filter(t -> !tpsRepository.findById(t.getFnr()).isPresent()).collect(Collectors.toSet());
        return new HashSet<>((Collection<? extends TpsModel>) tpsRepository.saveAll(reduced));
    }

    public Set<AaregModel> saveInAareg(Set<AaregRequest> data) {
        Set<AaregModel> reduced = data.stream()
                .filter(t -> !aaregRepository.findById(t.getFnr()).isPresent())
                .peek(t -> teamRepository.findByNavn(t.getEier()).ifPresent(t::setTeam))
                .map(t -> new AaregModel(
                        t.getFnr(),
                        t.getOrgId(),
                        t.getTeam()
                ))
                .collect(Collectors.toSet());
        return new HashSet<>((Collection<? extends AaregModel>) aaregRepository.saveAll(reduced));
    }

    public List<EregModel> saveInEreg(List<EregModel> data) {
        ArrayList<EregModel> returnValue = new ArrayList<>();
        eregRepository.saveAll(data).forEach(returnValue::add);
        return returnValue;
    }

    public Set<KrrModel> saveInKrr(Set<KrrModel> data) {
        Set<KrrModel> reduced = data.stream().filter(t -> !krrRepository.findById(t.getFnr()).isPresent()).collect(Collectors.toSet());
        return new HashSet<>((Collection<? extends KrrModel>) krrRepository.saveAll(reduced));
    }

    public List<EregModel> getEregData() {
        List<EregModel> data = new ArrayList<>();
        eregRepository.findAll().forEach(data::add);
        return data;
    }

    public List<String> deleteEreg(List<String> data) {
        return data.stream().map(eregRepository::deleteByOrgnr).map(EregModel::getOrgnr).collect(Collectors.toList());
    }
}
