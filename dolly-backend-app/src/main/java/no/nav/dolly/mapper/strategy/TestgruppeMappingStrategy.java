package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.security.sts.StsOidcService.getUserPrinciple;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class TestgruppeMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppe.class)
                .customize(new CustomMapper<Testgruppe, RsTestgruppe>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppe rsTestgruppe, MappingContext context) {
                        rsTestgruppe.setAntallIdenter(testgruppe.getTestidenter().size());
                        rsTestgruppe.setAntallIBruk(((Long) testgruppe.getTestidenter().stream().filter(ident -> isTrue(ident.getIBruk())).count()).intValue()); //NOSONAR
                        rsTestgruppe.setOpprettetAvNavIdent(testgruppe.getOpprettetAv().getBrukerId());
                        rsTestgruppe.setSistEndretAvNavIdent(testgruppe.getSistEndretAv().getBrukerId());
                        rsTestgruppe.setFavorittIGruppen(!testgruppe.getFavorisertAv().isEmpty());
                        rsTestgruppe.setErEierAvGruppe(getUserPrinciple().equalsIgnoreCase(testgruppe.getOpprettetAv().getBrukerId()));
                    }
                })
                .byDefault()
                .register();
    }
}