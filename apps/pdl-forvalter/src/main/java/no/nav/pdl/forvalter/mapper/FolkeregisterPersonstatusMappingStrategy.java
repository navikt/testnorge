package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.FolkeregisterPersonstatus;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class FolkeregisterPersonstatusMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(FolkeregisterPersonstatusDTO.class, FolkeregisterPersonstatus.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(FolkeregisterPersonstatusDTO kilde,
                                        FolkeregisterPersonstatus destinasjon, MappingContext context) {

                        destinasjon.setStatus(nonNull(kilde.getStatus()) ? kilde.getStatus() : FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.INAKTIV);
                    }
                })
                .byDefault()
                .register();
    }
}