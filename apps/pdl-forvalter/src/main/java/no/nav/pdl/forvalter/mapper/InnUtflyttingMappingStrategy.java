package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.domain.Folkeregistermetadata;
import no.nav.pdl.forvalter.domain.PdlInnflytting;
import no.nav.pdl.forvalter.domain.PdlUtflytting;
import no.nav.pdl.forvalter.dto.RsInnflytting;
import no.nav.pdl.forvalter.dto.RsUtflytting;
import org.springframework.stereotype.Component;

@Component
public class InnUtflyttingMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsInnflytting.class, PdlInnflytting.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInnflytting kilde, PdlInnflytting destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(Folkeregistermetadata.builder()
                                .gyldighetstidspunkt(kilde.getFlyttedato())
                                .build());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsUtflytting.class, PdlUtflytting.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsUtflytting kilde, PdlUtflytting destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(Folkeregistermetadata.builder()
                                .gyldighetstidspunkt(kilde.getFlyttedato())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}