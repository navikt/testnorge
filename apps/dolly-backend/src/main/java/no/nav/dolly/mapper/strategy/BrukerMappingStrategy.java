package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CurrentAuthentication.getAuthUser;

@Component
@RequiredArgsConstructor
public class BrukerMappingStrategy implements MappingStrategy {

    private final GetUserInfo getUserInfo;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bruker.class, RsBrukerAndGruppeId.class)
                .customize(new CustomMapper<Bruker, RsBrukerAndGruppeId>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBrukerAndGruppeId rsBruker, MappingContext context) {

                        if (!bruker.getFavoritter().isEmpty()) {
                            rsBruker.setFavoritter(bruker.getFavoritter().stream()
                                    .map(gruppe -> gruppe.getId().toString())
                                    .toList());
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Bruker.class, RsBrukerUtenFavoritter.class)
                .customize(new CustomMapper<Bruker, RsBrukerUtenFavoritter>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBrukerUtenFavoritter rsBruker, MappingContext context) {

                        if (nonNull(bruker.getEidAv())) {
                            rsBruker.setBrukerId(bruker.getEidAv().getBrukerId());
                            rsBruker.setBrukernavn(bruker.getEidAv().getBrukernavn());
                            rsBruker.setBrukertype(bruker.getBrukertype());
                            rsBruker.setEpost(bruker.getEidAv().getEpost());
                            rsBruker.setNavIdent(null);
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Bruker.class, RsBruker.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBruker rsBruker, MappingContext context) {

                        var brukerInfo = getAuthUser(getUserInfo);
                        rsBruker.setGrupper(brukerInfo.getGrupper());
                    }
                })
                .byDefault()
                .register();
    }

}
