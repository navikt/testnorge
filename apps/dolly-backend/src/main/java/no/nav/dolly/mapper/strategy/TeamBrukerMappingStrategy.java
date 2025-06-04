package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.service.BrukerService;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeamBrukerMappingStrategy implements MappingStrategy {

    private final BrukerService brukerService;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Team.class, RsTeam.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Team team, RsTeam rsTeam, MappingContext context) {
                        rsTeam.setBrukere(team.getBrukere().stream()
                                .map(Bruker::getBrukerId)
                                .collect(Collectors.toSet()));
                    }
                })
                .exclude("brukere")
                .byDefault()
                .register();

        factory.classMap(RsTeam.class, Team.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsTeam rsTeam, Team team, MappingContext context) {
                        var bruker = brukerService.fetchCurrentBrukerWithoutTeam();
                        team.setOpprettetAv(bruker);

                        if (!rsTeam.getBrukere().isEmpty()) {
                            team.setBrukere(rsTeam.getBrukere().stream()
                                    .map(brukerService::fetchBrukerWithoutTeam)
                                    .collect(Collectors.toSet()));
                        }
                    }
                })
                .exclude("brukere")
                .byDefault()
                .register();
    }
}