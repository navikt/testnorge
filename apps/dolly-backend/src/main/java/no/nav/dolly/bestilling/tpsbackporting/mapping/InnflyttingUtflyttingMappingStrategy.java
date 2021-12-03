package no.nav.dolly.bestilling.tpsbackporting.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Component;

@Component
public class InnflyttingUtflyttingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(InnflyttingDTO.class, TpsfBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(InnflyttingDTO source, TpsfBestilling target, MappingContext context) {
                        target.setInnvandretFraLand(source.getFraflyttingsland());
                        target.setInnvandretFraLandFlyttedato(source.getInnflyttingsdato());
                    }
                })
                .register();

        factory.classMap(UtflyttingDTO.class, TpsfBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(UtflyttingDTO source, TpsfBestilling target, MappingContext context) {
                        target.setUtvandretTilLand(source.getTilflyttingsland());
                        target.setUtvandretTilLandFlyttedato(source.getUtflyttingsdato());
                    }
                })
                .register();
    }
}
