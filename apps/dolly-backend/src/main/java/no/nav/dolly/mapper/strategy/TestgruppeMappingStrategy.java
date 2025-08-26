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

import static java.util.Objects.nonNull;

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
                        var opprettetAv = nonNull(testgruppe.getOpprettetAv()) ?
                                testgruppe.getOpprettetAv() :
                                alleBrukere.get(testgruppe.getOpprettetAvId());
                        var sistEndretAv = nonNull(testgruppe.getSistEndretAv()) ?
                                testgruppe.getSistEndretAv() :
                                alleBrukere.get(testgruppe.getSistEndretAvId());

                        rsTestgruppe.setAntallIdenter(antallIdenter);
                        rsTestgruppe.setAntallIBruk(antallIBruk);
                        rsTestgruppe.setAntallIdenter(antallIdenter);
                        rsTestgruppe.setAntallBestillinger(antallBestillinger);
                        rsTestgruppe.setErEierAvGruppe(bruker.getId().equals(testgruppe.getOpprettetAvId()));
                        rsTestgruppe.setOpprettetAv(mapperFacade.map(opprettetAv, RsBrukerUtenFavoritter.class));
                        rsTestgruppe.setSistEndretAv(mapperFacade.map(sistEndretAv, RsBrukerUtenFavoritter.class));
                        rsTestgruppe.setTags(testgruppe.getTags().stream()
                                .filter(tag -> Tags.DOLLY != tag)
                                .toList());
                    }
                })
                .byDefault()
                .register();
    }
}