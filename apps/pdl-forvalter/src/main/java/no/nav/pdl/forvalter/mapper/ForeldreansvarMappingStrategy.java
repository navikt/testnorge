package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlForeldreansvar;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Component
public class ForeldreansvarMappingStrategy implements MappingStrategy {

    private static LocalDate toDate(LocalDateTime timestamp) {

        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(ForeldreansvarDTO.class, PdlForeldreansvar.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ForeldreansvarDTO kilde,
                                        PdlForeldreansvar destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(FolkeregistermetadataDTO.builder()
                                .ajourholdstidspunkt(LocalDate.now())
                                .gyldighetstidspunkt(toDate(kilde.getGyldigFraOgMed()))
                                .opphoerstidspunkt(toDate(kilde.getGyldigTilOgMed()))
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}