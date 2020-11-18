package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.postgres.Testgruppe;
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
                        rsTestgruppe.setFavorittIGruppen(!testgruppe.getFavorisertAv().isEmpty());
                        rsTestgruppe.setErEierAvGruppe(getUserId().equalsIgnoreCase(testgruppe.getOpprettetAv().getBrukerId()));
                        rsTestgruppe.setErLaast(isTrue(rsTestgruppe.getErLaast()));
                    }
                })
                .byDefault()
                .register();
    }
}