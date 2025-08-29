package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndClaims;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class BrukerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bruker.class, RsBrukerAndClaims.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBrukerAndClaims rsBruker, MappingContext context) {

                        var brukerInfo = (UserInfoExtended) context.getProperty("brukerInfo");
                        var representererTeam = (Team) context.getProperty("representererTeam");
                        rsBruker.setGrupper(brukerInfo.grupper());

                        rsBruker.setFavoritter(getFavoritter(context));
                        if (nonNull(representererTeam) && nonNull(representererTeam.getId())) {
                            rsBruker.setRepresentererTeam(mapperFacade.map(representererTeam, RsTeamWithBrukere.class, context));
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Bruker.class, RsBruker.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBruker rsBruker, MappingContext context) {

                        rsBruker.setFavoritter(getFavoritter(context));
                    }
                })
                .byDefault()
                .register();
    }

    private static List<String> getFavoritter(MappingContext context) {

        var favoritter = (List<BrukerFavoritter>) (nonNull(context.getProperty("favoritter")) ?
                context.getProperty("favoritter") :
                emptyList());

        return favoritter.stream()
                .map(BrukerFavoritter::getGruppeId)
                .map(String::valueOf)
                .toList();
    }
}
