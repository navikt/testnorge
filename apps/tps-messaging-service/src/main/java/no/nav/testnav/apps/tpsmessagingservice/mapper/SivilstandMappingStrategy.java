package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SivilstandDTO;
import no.nav.tps.ctg.s610.domain.S610PersonType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Component
public class SivilstandMappingStrategy implements MappingStrategy {

    private static final String POST_NORGE = "POST";
    private static final String NORGE = "NOR";

    private static String skipLeadZeros(String number) {

        return StringUtils.isNumeric(number) ?
                Integer.valueOf(number).toString() :
                number;
    }

    private static LocalDateTime getDate(String dato) {

        return nonNull(dato) ? LocalDate.parse(dato).atStartOfDay() : null;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(S610PersonType.SivilstandDetalj.class, SivilstandDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(S610PersonType.SivilstandDetalj source, SivilstandDTO target, MappingContext context) {

                        target.setSivilstand(source.getKodeSivilstand().name());
                        target.setSivilstandRegdato(getDate(source.getDatoSivilstand()));
                    }
                })
                .register();
    }
}
