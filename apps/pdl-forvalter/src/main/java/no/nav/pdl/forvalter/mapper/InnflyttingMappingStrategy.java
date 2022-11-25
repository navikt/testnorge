package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlInnflytting;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InnflyttingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(InnflyttingDTO.class, PdlInnflytting.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(InnflyttingDTO kilde,
                                        PdlInnflytting destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(FolkeregistermetadataDTO.builder()
                                .ajourholdstidspunkt(LocalDateTime.now())
                                .gyldighetstidspunkt(kilde.getInnflyttingsdato())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}