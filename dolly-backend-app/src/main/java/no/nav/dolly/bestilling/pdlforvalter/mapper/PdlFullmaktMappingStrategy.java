package no.nav.dolly.bestilling.pdlforvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmakt;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmaktHistorikk;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PdlFullmaktMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlFullmaktHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlFullmaktHistorikk historikk, MappingContext context) {

                        person.getFullmakt().forEach(fullmakt -> {

                            PdlFullmakt pdlFullmakt = PdlFullmakt.builder()
                                    .fullmektig(fullmakt.getFullmektig().getIdent())
                                    .kilde(fullmakt.getKilde())
                                    .omraader(fullmakt.getOmraader())
                                    .gyldigFom(fullmakt.getGyldigFom())
                                    .gyldigTom(fullmakt.getGyldigTom())
                                    .build();

                            historikk.getFullmakter().add(pdlFullmakt);
                        });
                    }
                })
                .register();
    }
}