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

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
public class TestgruppeMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppe.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppe rsTestgruppe, MappingContext context) {

                        var bruker = (Bruker) context.getProperty("bruker");
                        var antallIdenter = (int) context.getProperty("antallIdenter");
                        var antallBestillinger = (int) context.getProperty("antallBestillinger");
                        var antallIBruk = (int) context.getProperty("antallIBruk");

                        rsTestgruppe.setAntallIdenter(antallIdenter);
                        rsTestgruppe.setAntallIBruk(antallIBruk);
                        rsTestgruppe.setAntallIdenter(antallIdenter);
                        rsTestgruppe.setAntallBestillinger(antallBestillinger);
                        rsTestgruppe.setErEierAvGruppe(bruker.getId().equals(testgruppe.getOpprettetAvId()));
                        rsTestgruppe.setTags(testgruppe.getTags().stream()
                                .filter(tag -> Tags.DOLLY != tag)
                                .toList());
                    }
                })
                .byDefault()
                .register();
    }
}