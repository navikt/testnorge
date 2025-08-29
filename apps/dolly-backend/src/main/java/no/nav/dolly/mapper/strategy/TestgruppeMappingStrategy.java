package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.service.BrukerService;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
public class TestgruppeMappingStrategy implements MappingStrategy {

    private final GetUserInfo getUserInfo;
    private final BrukerService brukerService;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppe.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppe rsTestgruppe, MappingContext context) {

                        var brukerId = nonNull(context.getProperty("brukerId")) ? (String) context.getProperty("brukerId") : getUserId(getUserInfo);
                        var bruker = brukerService.fetchBrukerOrTeamBruker(brukerId);

                        rsTestgruppe.setAntallIdenter(testgruppe.getTestidenter().size());
                        rsTestgruppe.setAntallIBruk((int) testgruppe.getTestidenter().stream()
                                .filter(ident -> isTrue(ident.getIBruk()))
                                .count());
                        rsTestgruppe.setErEierAvGruppe(bruker.getBrukerId().equals(testgruppe.getOpprettetAv().getBrukerId()));
                        rsTestgruppe.setErLaast(isTrue(rsTestgruppe.getErLaast()));
                        rsTestgruppe.setTags(nonNull(testgruppe.getTags()) ? testgruppe.getTags().stream()
                                .filter(tag -> Tags.DOLLY != tag)
                                .toList() : Collections.emptyList()
                        );
                    }
                })
                .byDefault()
                .register();
    }
}