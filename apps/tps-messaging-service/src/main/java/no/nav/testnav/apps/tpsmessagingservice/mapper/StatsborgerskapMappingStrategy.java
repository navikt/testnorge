package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.StatsborgerskapDTO;
import no.nav.tps.ctg.s610.domain.S610PersonType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class StatsborgerskapMappingStrategy implements MappingStrategy {

    private static LocalDateTime getDate(String dato) {

        return isNotBlank(dato) ? LocalDate.parse(dato).atStartOfDay() : null;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(S610PersonType.StatsborgerskapDetalj.class, StatsborgerskapDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(S610PersonType.StatsborgerskapDetalj source, StatsborgerskapDTO target, MappingContext context) {

                        target.setStatsborgerskap(source.getKodeStatsborgerskap());
                        target.setStatsborgerskapRegdato(getDate(source.getDatoStatsborgerskap()));
                    }
                })
                .register();
    }
}
