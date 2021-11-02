package no.nav.dolly.bestilling.pdlforvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmakt;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmaktHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.util.NullcheckUtil;
import org.springframework.stereotype.Component;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

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
                                    .motpartsPersonident(fullmakt.getFullmektig().getIdent())
                                    .kilde(NullcheckUtil.nullcheckSetDefaultValue(fullmakt.getKilde(), CONSUMER))
                                    .master(Master.PDL)
                                    .omraader(fullmakt.getOmraader())
                                    .gyldigFraOgMed(fullmakt.getGyldigFom())
                                    .gyldigTilOgMed(fullmakt.getGyldigTom())
                                    .build();

                            historikk.getFullmakter().add(pdlFullmakt);
                        });
                    }
                })
                .register();
    }
}
