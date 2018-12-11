package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.domain.resultset.RsBrukerTeamAndGruppeIDs;
import no.nav.dolly.domain.resultset.RsTeamMedIdOgNavn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class BrukerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bruker.class, RsBrukerTeamAndGruppeIDs.class)
                .customize(new CustomMapper<Bruker, RsBrukerTeamAndGruppeIDs>() {
                    @Override public void mapAtoB(Bruker bruker, RsBrukerTeamAndGruppeIDs rsBrukerTeamAndGruppeIDs, MappingContext context) {
                        rsBrukerTeamAndGruppeIDs.setNavIdent(bruker.getNavIdent());

                        if (bruker.getFavoritter() != null) {
                            rsBrukerTeamAndGruppeIDs.setFavoritter(bruker.getFavoritter().stream().map(gruppe -> gruppe.getId().toString()).collect(Collectors.toList()));
                        }

                        List<RsTeamMedIdOgNavn> teams = new ArrayList<>();
                        if (bruker.getTeams() != null) {
                            teams = mapperFacade.mapAsList(bruker.getTeams(), RsTeamMedIdOgNavn.class);
                        }
                        rsBrukerTeamAndGruppeIDs.setTeams(teams);
                    }
                })
                .register();

    }
}
