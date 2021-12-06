package no.nav.dolly.bestilling.tpsbackporting.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class AdressekbeskyttelseMappingStrategy implements MappingStrategy {

    private static String getSpesreg(AdressebeskyttelseDTO.AdresseBeskyttelse beskyttelse) {

        if (isNull(beskyttelse)) {
            return null;
        }
        return switch (beskyttelse) {
            case STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND -> "SPSF";
            case FORTROLIG -> "SPFO";
            default -> null;
        };
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(AdressebeskyttelseDTO.class, TpsfBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(AdressebeskyttelseDTO source, TpsfBestilling target, MappingContext context) {
                        target.setSpesreg(getSpesreg(source.getGradering()));
                    }
                })
                .register();
    }
}
