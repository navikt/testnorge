package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final VarighetRepository varighetRepository;

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
        Varighet.VarighetBuilder varighetBuilder = Varighet.builder()
                .bestilt(new Date(Calendar.getInstance().getTime().getTime()))
                .ttl(Period.of(1, 0, 0));
        return data.stream()
                .filter(aaregRequest -> !aaregRepository.findById(aaregRequest.getFnr()).isPresent())
                .peek(aaregRequest -> teamRepository.findByNavn(aaregRequest.getEier()).ifPresent(team -> {
                    aaregRequest.setTeam(team);
                    Varighet varighet = varighetRepository.save(
                            varighetBuilder
                                    .team(team)
                                    .build()
                    );
                    team.getVarigheter().add(varighet);
                    teamRepository.save(team);
                    aaregRepository.save(new AaregModel(
                            aaregRequest.getFnr(),
                            aaregRequest.getOrgId(),
                            aaregRequest.getTeam(),
                            varighet
                    ));
                }))
                .collect(Collectors.toSet());
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

    public Team getTeam(String name) {
        return teamRepository.findByNavn(name).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ingen team med dette navnet"));
    }
}
