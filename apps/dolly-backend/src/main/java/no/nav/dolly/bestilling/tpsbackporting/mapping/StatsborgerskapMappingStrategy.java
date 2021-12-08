package no.nav.dolly.bestilling.tpsbackporting.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.stereotype.Component;

@Component
public class StatsborgerskapMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(StatsborgerskapDTO.class, TpsfBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(StatsborgerskapDTO source, TpsfBestilling target, MappingContext context) {
                        target.setStatsborgerskap(source.getLandkode());
                        target.setStatsborgerskapRegdato(source.getGyldigFraOgMed());
                        target.setStatsborgerskapTildato(source.getGyldigTilOgMed());
                    }
                })
                .register();
    }
}
