package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlInnflytting;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Component
public class InnflyttingMappingStrategy implements MappingStrategy {

    private static LocalDate toDate(LocalDateTime timestamp) {

        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(InnflyttingDTO.class, PdlInnflytting.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(InnflyttingDTO kilde,
                                        PdlInnflytting destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(FolkeregistermetadataDTO.builder()
                                .ajourholdstidspunkt(LocalDate.now())
                                .gyldighetstidspunkt(toDate(kilde.getInnflyttingsdato()))
                                .gjeldende(kilde.isGjeldende())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}