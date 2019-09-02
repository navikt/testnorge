package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.Date;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.Ownable;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.Team;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.model.Varighet;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;
import no.nav.registre.sdForvalter.database.repository.TeamRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;
import no.nav.registre.sdForvalter.database.repository.VarighetRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticDataService {

    private final AaregRepository aaregRepository;
    private final TpsRepository tpsRepository;
    private final KrrRepository krrRepository;
    private final EregRepository eregRepository;
    private final TeamRepository teamRepository;
    private final VarighetRepository varighetRepository;

    @Value("${tps.statisk.avspillergruppeId}")
    private Long playgroupStaticData;

    public Set<Team> getAllTeams() {
        return new HashSet<>((Collection<? extends Team>) teamRepository.findAll());
    }

    public Team getTeam(String name) {
        return teamRepository.findByNavn(name).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ingen team med dette navnet"));
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

    public Set<TpsModel> saveInTps(Set<TpsModel> data, String eier) {
        Varighet.VarighetBuilder varighetBuilder = getDefaultVarighetBuilder();
        return data.stream()
                .filter(tpsModel -> !tpsRepository.findById(tpsModel.getFnr()).isPresent())
                .map(tpsModel -> (TpsModel) saveIfNotPresent(tpsModel, tpsRepository, varighetBuilder, eier))
                .collect(Collectors.toSet());
    }

    public Set<AaregModel> saveInAareg(Set<AaregModel> data, String eier) {
        Varighet.VarighetBuilder varighetBuilder = getDefaultVarighetBuilder();
        return data.stream()
                .filter(aaregModel -> !aaregRepository.findById(aaregModel.getFnr()).isPresent())
                .map(aaregModel -> (AaregModel) saveIfNotPresent(aaregModel, aaregRepository, varighetBuilder, eier))
                .collect(Collectors.toSet());
    }

    public Set<EregModel> saveInEreg(Set<EregModel> data, String eier) {
        Varighet.VarighetBuilder varighetBuilder = getDefaultVarighetBuilder();
        return data.stream().filter(eregModel -> eregRepository.findByOrgnr(eregModel.getOrgnr()).isPresent())
                .map(eregModel -> (EregModel) saveIfNotPresent(eregModel, eregRepository, varighetBuilder, eier))
                .collect(Collectors.toSet());
    }

    public Set<KrrModel> saveInKrr(Set<KrrModel> data, String eier) {
        Varighet.VarighetBuilder varighetBuilder = getDefaultVarighetBuilder();
        return data.stream().filter(krrModel -> krrRepository.findByFnr(krrModel.getFnr()).isPresent())
                .map(krrModel -> (KrrModel) saveIfNotPresent(krrModel, krrRepository, varighetBuilder, eier))
                .collect(Collectors.toSet());
    }

    public List<EregModel> getEregData() {
        List<EregModel> data = new ArrayList<>();
        eregRepository.findAll().forEach(data::add);
        return data;
    }

    public List<String> deleteEreg(List<String> data) {
        return data.stream().map(eregRepository::deleteByOrgnr).map(EregModel::getOrgnr).collect(Collectors.toList());
    }

    private Varighet.VarighetBuilder getDefaultVarighetBuilder() {
        return Varighet.builder()
                .bestilt(new Date(Calendar.getInstance().getTime().getTime()))
                .ttl(Period.of(1, 0, 0));
    }

    private Ownable saveIfNotPresent(Ownable entity, CrudRepository repository, Varighet.VarighetBuilder varighetBuilder, String eier) {
        return (Ownable) teamRepository.findByNavn(eier).map(team -> {
            entity.setTeam(team);
            Varighet varighet = varighetRepository.save(
                    varighetBuilder
                            .team(team)
                            .build()
            );
            team.getVarigheter().add(varighet);
            teamRepository.save(team);
            return repository.save(entity);
        }).get();
    }
}
