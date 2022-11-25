package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlForeldreansvar;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ForeldreansvarMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(ForeldreansvarDTO.class, PdlForeldreansvar.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ForeldreansvarDTO kilde,
                                        PdlForeldreansvar destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(FolkeregistermetadataDTO.builder()
                                .ajourholdstidspunkt(LocalDateTime.now())
                                .gyldighetstidspunkt(kilde.getGyldigFraOgMed())
                                .opphoerstidspunkt(kilde.getGyldigTilOgMed())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}