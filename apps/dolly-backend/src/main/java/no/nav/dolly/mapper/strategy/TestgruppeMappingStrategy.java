package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

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
                        var antallIdenter = (Integer) context.getProperty("antallIdenter");
                        var antallBestillinger = (Integer) context.getProperty("antallBestillinger");
                        var antallIBruk = (Integer) context.getProperty("antallIBruk");
                        var alleBrukere = (Map<Long, Bruker>) context.getProperty("alleBrukere");

                        rsTestgruppe.setAntallIdenter(antallIdenter);
                        rsTestgruppe.setAntallIBruk(antallIBruk);
                        rsTestgruppe.setAntallIdenter(antallIdenter);
                        rsTestgruppe.setAntallBestillinger(antallBestillinger);
                        rsTestgruppe.setErEierAvGruppe(bruker.getId().equals(testgruppe.getOpprettetAvId()));
                        rsTestgruppe.setOpprettetAv(mapperFacade.map(alleBrukere.get(testgruppe.getOpprettetAvId()), RsBrukerUtenFavoritter.class));
                        rsTestgruppe.setSistEndretAv(mapperFacade.map(alleBrukere.get(testgruppe.getSistEndretAvId()), RsBrukerUtenFavoritter.class));
                        rsTestgruppe.setTags(testgruppe.getTags().stream()
                                .filter(tag -> Tags.DOLLY != tag)
                                .toList());
                    }
                })
                .byDefault()
                .register();
    }
}