package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlSivilstand;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Component;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SAMBOER;

@Component
public class SivilstandMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SivilstandDTO.class, PdlSivilstand.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SivilstandDTO kilde, PdlSivilstand destinasjon, MappingContext context) {

                        if (kilde.getType() == SAMBOER) {
                            destinasjon.setRelatertVedSivilstand(null);
                        }
                    }
                })
                .byDefault()
                .register();
    }
}