package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.Folkeregistermetadata;
import no.nav.pdl.forvalter.dto.PdlUtflytting;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Component
public class UtflyttingMappingStrategy implements MappingStrategy {

    private static LocalDate toDate(LocalDateTime timestamp) {

        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(UtflyttingDTO.class, PdlUtflytting.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(UtflyttingDTO kilde,
                                        PdlUtflytting destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(Folkeregistermetadata.builder()
                                .ajourholdstidspunkt(LocalDate.now())
                                .gyldighetstidspunkt(toDate(kilde.getGyldigFraOgMed()))
                                .opphoerstidspunkt(toDate(kilde.getGyldigTilOgMed()))
                                .gjeldende(nonNull(kilde.getGyldigFraOgMed()) || nonNull(kilde.getGyldigTilOgMed()))
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}