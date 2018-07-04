package no.nav.mapper.stratergy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.jpa.Team;
import no.nav.mapper.MappingStrategy;
import no.nav.resultSet.RsBruker;
import no.nav.resultSet.RsTeam;
import no.nav.resultSet.RsTestgruppe;

import org.springframework.stereotype.Component;

@Component
public class TeamMappingStrategy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Team.class, RsTeam.class)
                .customize(new CustomMapper<Team, RsTeam>() {
                    @Override
                    public void mapAtoB(Team team, RsTeam rsTeam, MappingContext context) {
                        rsTeam.setBeskrivelse(team.getBeskrivelse());
                        rsTeam.setDatoOpprettet(team.getDatoOpprettet());
                        //rsTeam.setGrupper(mapperFacade.mapAsSet(team.getGrupper(), RsTestgruppe.class));
                        rsTeam.setNavn(team.getNavn());
                        rsTeam.setId(team.getId());
                        rsTeam.setEierNavIdent(team.getEier().getNavIdent());
                        rsTeam.setMedlemmer(mapperFacade.mapAsSet(team.getMedlemmer(), RsBruker.class));
                    }
                })
                .register();

    }
}
