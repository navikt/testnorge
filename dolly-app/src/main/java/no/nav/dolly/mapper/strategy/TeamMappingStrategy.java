package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamMedIdOgNavn;
import no.nav.dolly.domain.resultset.entity.team.RsTeamMedMedlemmerUtenGrupper;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUtvidet;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class TeamMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Team.class, RsTeam.class)
                .customize(new CustomMapper<Team, RsTeam>() {
                    @Override
                    public void mapAtoB(Team team, RsTeam rsTeam, MappingContext context) {
                        rsTeam.setAntallMedlemmer(team.getMedlemmer().size());
                        rsTeam.setEierNavIdent(team.getEier().getNavIdent());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Team.class, RsTeamUtvidet.class)
                .customize(new CustomMapper<Team, RsTeamUtvidet>() {
                    @Override
                    public void mapAtoB(Team team, RsTeamUtvidet rsTeam, MappingContext context) {
                        rsTeam.setAntallMedlemmer(team.getMedlemmer().size());
                        rsTeam.setGrupper(mapperFacade.mapAsList(team.getGrupper(), RsTestgruppe.class));
                        rsTeam.setEierNavIdent(team.getEier().getNavIdent());
                        rsTeam.setMedlemmer(mapperFacade.mapAsList(team.getMedlemmer(), RsBruker.class));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Team.class, RsTeamMedMedlemmerUtenGrupper.class)
                .customize(new CustomMapper<Team, RsTeamMedMedlemmerUtenGrupper>() {
                    @Override
                    public void mapAtoB(Team team, RsTeamMedMedlemmerUtenGrupper rsTeam, MappingContext context) {
                        rsTeam.setEierNavIdent(team.getEier().getNavIdent());
                        rsTeam.setMedlemmer(mapperFacade.mapAsSet(team.getMedlemmer(), RsBruker.class));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Team.class, RsTeamMedIdOgNavn.class)
                .byDefault()
                .register();
    }
}
