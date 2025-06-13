package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class BrukerMappingStrategy implements MappingStrategy {

    private final GetUserInfo getUserInfo;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bruker.class, RsBrukerAndGruppeId.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBrukerAndGruppeId rsBruker, MappingContext context) {

                        var favoritter = (List<BrukerFavoritter>) (nonNull(context.getProperty("favoritter")) ?
                                context.getProperty("favoritter") :
                                emptyList());
                        var brukerInfo = (UserInfoExtended) context.getProperty("brukerInfo");
                        rsBruker.setFavoritter(favoritter.stream()
                                .map(BrukerFavoritter::getId)
                                .map(BrukerFavoritter.BrukerFavoritterId::getGruppeId)
                                .map(gruppeId -> Long.toString(gruppeId))
                                .toList());
                        rsBruker.setG
                    }
                }
    })
            .

    byDefault()
                .

    register();

        factory.classMap(Bruker .class,RsBruker .class)
            .

    customize(new CustomMapper<>() {
        @Override
        public void mapAtoB (Bruker bruker, RsBruker rsBruker, MappingContext context){


            getUserInfo.call()
                    .flatMap()
            rsBruker.setGrupper(brukerInfo.getGrupper());
        }
    })
            .

    byDefault()
                .

    register();
}

}
