package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlTilrettelagtKommunikasjon;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import org.springframework.stereotype.Component;

@Component
public class TilrettelagtKommunikasjonMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(TilrettelagtKommunikasjonDTO.class, PdlTilrettelagtKommunikasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TilrettelagtKommunikasjonDTO kilde, PdlTilrettelagtKommunikasjon kommunikasjon, MappingContext context) {

                        kommunikasjon.setTalespraaktolk(PdlTilrettelagtKommunikasjon.Tolk.builder()
                                .spraak(kilde.getSpraakForTaletolk())
                                .build());
                        kommunikasjon.setTegnspraaktolk(PdlTilrettelagtKommunikasjon.Tolk.builder()
                                .spraak(kilde.getSpraakForTegnspraakTolk())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}