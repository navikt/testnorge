package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class TestgruppeMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppe.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppe rsTestgruppe, MappingContext context) {

                        rsTestgruppe.setAntallIdenter(testgruppe.getTestidenter().size());
                        rsTestgruppe.setAntallIBruk((int) testgruppe.getTestidenter().stream()
                                .filter(ident -> isTrue(ident.getIBruk()))
                                .count());
                        rsTestgruppe.setFavorittIGruppen(!testgruppe.getFavorisertAv().isEmpty());
                        rsTestgruppe.setErEierAvGruppe(nonNull(context.getProperty("brukerId")) && (context.getProperty("brukerId")).equals(getBrukerId(testgruppe.getOpprettetAv())));
                        rsTestgruppe.setErLaast(isTrue(rsTestgruppe.getErLaast()));
                        rsTestgruppe.setTags(testgruppe.getTags().stream()
                                .filter(tag -> Tags.DOLLY != tag)
                                .toList()
                        );
                    }
                })
                .byDefault()
                .register();
    }

    private static String getBrukerId(Bruker bruker) {

        if (isNotBlank(bruker.getBrukerId())) {
            return bruker.getBrukerId();
        } else {
            return nonNull(bruker.getEidAv()) ? bruker.getEidAv().getBrukerId() : bruker.getNavIdent();
        }
    }
}