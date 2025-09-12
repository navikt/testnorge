package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class TeamBrukerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Team.class, RsTeamWithBrukere.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Team team, RsTeamWithBrukere rsTeamWithBrukere, MappingContext context) {

                        var teamMedlemmer = (List<TeamBruker>) context.getProperty("teamMedlemmer");

                        rsTeamWithBrukere.setOpprettet(team.getOpprettetTidspunkt());
                        rsTeamWithBrukere.setBrukerId((String) context.getProperty("brukerId"));

                        if (nonNull(teamMedlemmer)) {
                            rsTeamWithBrukere.setBrukere(new HashSet<>(
                                    mapperFacade.mapAsList(teamMedlemmer,
                                            RsBrukerUtenFavoritter.class, context)));
                        }
                    }
                })
                .byDefault()
                .register();
    }
}