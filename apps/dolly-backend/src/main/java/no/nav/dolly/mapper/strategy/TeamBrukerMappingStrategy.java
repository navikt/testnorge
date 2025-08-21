package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamBrukerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Team.class, RsTeamWithBrukere.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Team team, RsTeamWithBrukere rsTeamWithBrukere, MappingContext context) {

                        rsTeamWithBrukere.setOpprettet(team.getOpprettetTidspunkt());
                        rsTeamWithBrukere.setBrukerId((String) context.getProperty("brukerId"));
                    }
                })
                .byDefault()
                .register();
    }
}