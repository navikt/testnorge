package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.FolkeregisterPersonstatus;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Component
public class FolkeregisterPersonstatusMappingStrategy implements MappingStrategy {

    private static LocalDate toDate(LocalDateTime timestamp) {

        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(FolkeregisterPersonstatusDTO.class, FolkeregisterPersonstatus.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(FolkeregisterPersonstatusDTO kilde,
                                        FolkeregisterPersonstatus destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(FolkeregistermetadataDTO.builder()
                                .ajourholdstidspunkt(LocalDate.now())
                                .gyldighetstidspunkt(toDate(kilde.getGyldigFraOgMed()))
                                .opphoerstidspunkt(toDate(kilde.getGyldigTilOgMed()))
                                .gjeldende(kilde.isGjeldende())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}