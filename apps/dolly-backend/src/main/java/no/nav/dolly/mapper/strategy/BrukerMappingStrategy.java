package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class BrukerMappingStrategy implements MappingStrategy {

    private static final String FAVORITTER = "favoritter";
    
    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Bruker.class, RsBruker.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBruker rsBruker, MappingContext context) {

                        context.setProperty("bruker", bruker);
                        var favoritter = (List<Testgruppe>) context.getProperty(FAVORITTER);
                        rsBruker.setGrupper(((UserInfoExtended) context.getProperty("brukerInfo")).grupper());
                        rsBruker.setFavoritter(favoritter.stream()
                                .map(favoritt -> mapperFacade.map(favoritt, RsTestgruppe.class, context))
                                .toList());
                        var representererTeam = (Team) context.getProperty("representererTeam");
                        if (nonNull(representererTeam) && nonNull(representererTeam.getId())) {
                            rsBruker.setRepresentererTeam(mapperFacade.map(representererTeam, RsTeamWithBrukere.class, context));
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
