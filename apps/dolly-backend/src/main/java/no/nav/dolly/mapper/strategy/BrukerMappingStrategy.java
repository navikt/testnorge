package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.service.BrukerService;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.stereotype.Component;

import static no.nav.dolly.util.CurrentAuthentication.getAuthUser;

@Component
@RequiredArgsConstructor
public class BrukerMappingStrategy implements MappingStrategy {

    private final GetUserInfo getUserInfo;
    private final BrukerService brukerService;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bruker.class, RsBrukerAndGruppeId.class)
                .customize(new CustomMapper<>() {
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

        factory.classMap(Bruker.class, RsBruker.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBruker rsBruker, MappingContext context) {

                        var brukerInfo = getAuthUser(getUserInfo);
                        var aktivBruker = brukerService.fetchBrukerOrTeamBruker(brukerInfo.getBrukerId());
                        rsBruker.setFavoritter(mapperFacade.mapAsList(aktivBruker.getFavoritter(), RsTestgruppe.class));

                        rsBruker.setGrupper(brukerInfo.getGrupper());
                    }
                })
                .byDefault()
                .register();
    }

}
