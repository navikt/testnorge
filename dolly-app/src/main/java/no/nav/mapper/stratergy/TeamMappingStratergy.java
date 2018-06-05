package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFactory;
import no.nav.api.resultSet.RsTeam;
import no.nav.jpa.Team;
import no.nav.mapper.MappingStrategy;

import org.springframework.stereotype.Component;

@Component
public class TeamMappingStratergy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Team.class, RsTeam.class)
                .byDefault()
                .register();

        factory.classMap(RsTeam.class, Team.class)
                .byDefault()
                .register();
    }
}
