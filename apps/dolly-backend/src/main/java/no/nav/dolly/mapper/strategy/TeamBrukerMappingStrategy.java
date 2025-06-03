package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
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
                        if (team.getOpprettetAv() != null) {
                            rsTeam.setOpprettetAv(factory.getMapperFacade().map(team.getOpprettetAv(), RsBruker.class));
                        }

                        if (!team.getBrukere().isEmpty()) {
                            rsTeam.setBrukere(team.getBrukere().stream()
                                    .map(bruker -> factory.getMapperFacade().map(bruker, RsBruker.class))
                                    .map(RsBruker::getBrukerId)
                                    .collect(Collectors.toSet()));
                        }
                    }
                })
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
                .byDefault()
                .register();
    }
}