package no.nav.dolly.mapper.stratergy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.domain.resultset.RsTeamMedIdOgNavn;
import no.nav.dolly.domain.resultset.RsTeamMedMedlemmerUtenGrupper;
import no.nav.dolly.domain.resultset.RsTestgruppe;

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
                        rsTeam.setGrupper(mapperFacade.mapAsSet(team.getGrupper(), RsTestgruppe.class));
                        rsTeam.setNavn(team.getNavn());
                        rsTeam.setId(team.getId());
                        rsTeam.setEierNavIdent(team.getEier().getNavIdent());
                        rsTeam.setMedlemmer(mapperFacade.mapAsSet(team.getMedlemmer(), RsBruker.class));
                    }
                })
                .register();

        factory.classMap(Team.class, RsTeamMedMedlemmerUtenGrupper.class)
                .customize(new CustomMapper<Team, RsTeamMedMedlemmerUtenGrupper>() {
                    @Override
                    public void mapAtoB(Team team, RsTeamMedMedlemmerUtenGrupper rsTeam, MappingContext context) {
                        rsTeam.setBeskrivelse(team.getBeskrivelse());
                        rsTeam.setDatoOpprettet(team.getDatoOpprettet());
                        rsTeam.setNavn(team.getNavn());
                        rsTeam.setId(team.getId());
                        rsTeam.setEierNavIdent(team.getEier().getNavIdent());
                        rsTeam.setMedlemmer(mapperFacade.mapAsSet(team.getMedlemmer(), RsBruker.class));
                    }
                })
                .register();

        factory.classMap(Team.class, RsTeamMedIdOgNavn.class)
                .customize(new CustomMapper<Team, RsTeamMedIdOgNavn>() {
                    @Override public void mapAtoB(Team team, RsTeamMedIdOgNavn rsTeamMedIdOgNavn, MappingContext context) {
                        rsTeamMedIdOgNavn.setId(team.getId());
                        rsTeamMedIdOgNavn.setNavn(team.getNavn());
                    }
                })
                .register();
    }
}
