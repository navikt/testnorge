package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.util.CurrentNavIdentFetcher.getLoggedInNavIdent;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.util.List;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
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
                    }

                    private boolean isMedlem(List<Bruker> brukere) {
                        boolean isMedlem = false;
                        for (Bruker bruker : brukere) {
                            if (getLoggedInNavIdent().equalsIgnoreCase(bruker.getBrukerId())) {
                                isMedlem = true;
                                break;
                            }
                        }
                        return isMedlem;
                    }
                })
                .byDefault()
                .register();
    }
}