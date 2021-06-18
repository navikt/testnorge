package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.domain.InnflyttingDTO;
import no.nav.pdl.forvalter.domain.UtflyttingDTO;
import no.nav.pdl.forvalter.dto.Folkeregistermetadata;
import no.nav.pdl.forvalter.dto.PdlInnflytting;
import no.nav.pdl.forvalter.dto.PdlUtflytting;
import org.springframework.stereotype.Component;

@Component
public class InnUtflyttingMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(InnflyttingDTO.class, PdlInnflytting.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(InnflyttingDTO kilde, PdlInnflytting destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(Folkeregistermetadata.builder()
                                .gyldighetstidspunkt(kilde.getFlyttedato())
                                .build());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(UtflyttingDTO.class, PdlUtflytting.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(UtflyttingDTO kilde, PdlUtflytting destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(Folkeregistermetadata.builder()
                                .gyldighetstidspunkt(kilde.getFlyttedato())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}