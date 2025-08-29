package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
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

        factory.classMap(Bruker.class, RsBrukerUtenFavoritter.class)
                .byDefault()
                .register();

        factory.classMap(RsTeam.class, Team.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsTeam rsTeam, Team team, MappingContext context) {
                        var bruker = brukerService.fetchCurrentBrukerWithoutTeam();
                        team.setOpprettetAv(bruker);

                        if (!rsTeam.getBrukere().isEmpty()) {
                            try {

                                team.setBrukere(rsTeam.getBrukere().stream()
                                        .map(brukerService::fetchBrukerWithoutTeam)
                                        .collect(Collectors.toSet()));
                            } catch (Exception e) {
                                throw new IllegalArgumentException("Klarte ikke å utlede en eller flere bruker(e) fra brukerId: " + rsTeam.getBrukere(), e);
                            }
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Team.class, RsTeamWithBrukere.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Team team, RsTeamWithBrukere rsTeamWithBrukere, MappingContext context) {
                        var teamBruker = brukerService.fetchBrukerById(team.getBrukerId());

                        rsTeamWithBrukere.setBrukerId(teamBruker.getBrukerId());
                        rsTeamWithBrukere.setBrukere(team.getBrukere().stream()
                                .map(bruker -> mapperFacade.map(bruker, RsBrukerUtenFavoritter.class))
                                .collect(Collectors.toSet()));
                    }
                })
                .byDefault()
                .register();
    }
}