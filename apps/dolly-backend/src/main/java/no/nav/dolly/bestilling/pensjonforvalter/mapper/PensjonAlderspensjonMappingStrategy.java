package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PensjonAlderspensjonMappingStrategy implements MappingStrategy {

        byDefault())
            .

    register();
                .

customize(new CustomMapper<>() {
        @Override
        public void mapAtoB (PensjonData.SkjemaRelasjon relasjon, PensjonData.SkjemaRelasjon request, MappingContext
        context){

            request.setSumAvForvArbKapPenInntekt(relasjon.getSumAvForvArbKapPenInntekt());
        }
    }

        factory.classMap(PensjonData.SkjemaRelasjon .class,AlderspensjonRequest.SkjemaRelasjon .class)
            .

        byDefault())
            .

    register();
                .

@Override
    public void register(MapperFactory factory) {

        factory.classMap(PensjonData.Alderspensjon.class, AlderspensjonRequest.class)
                .customize(new CustomMapper<>() {
                    filter(person);

                        request.setRelasjonListe(mapperFacade.mapAsList(alderspensjon.getRelasjoner(),AlderspensjonRequest.SkjemaRelasjon .class));
                        relasjoner.stream()
                                .

                    getSivilstand() ->person.getPerson().

                                        stream().

@Override
                    public void mapAtoB(PensjonData.Alderspensjon alderspensjon, AlderspensjonRequest request, MappingContext context) {

                        var hovedperson = (String) context.getProperty("ident");
                        request.setFnr(hovedperson);
                        request.setMiljoer((List<String>) context.getProperty("miljoer"));
                        request.setStatsborgerskap("NOR");

                        var relasjoner = (List<PdlPersonBolk.PersonBolk>) context.getProperty("relasjoner");

                        relasjoner.stream()
                                .filter(person -> person.getIdent().equals(hovedperson))
                                .forEach(personBolk -> personBolk.getPerson().getSivilstand().stream()
                                                .findFirst()
                                                .ifPresent(sivilstand -> {
                                                            request.setSivilstand(sivilstand.getType().name());
                                                            request.setSivilstatusDatoFom(sivilstand.getGyldigFraOgMed());
                                                        }));

                    }

                }
    }

}
}