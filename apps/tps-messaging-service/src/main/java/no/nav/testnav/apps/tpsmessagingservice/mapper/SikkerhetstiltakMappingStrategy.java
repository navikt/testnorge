package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SikkerhetTiltakDTO;
import no.nav.tps.ctg.s610.domain.TsikkerhetsTiltakS610;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class SikkerhetstiltakMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(TsikkerhetsTiltakS610.class, SikkerhetTiltakDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TsikkerhetsTiltakS610 source, SikkerhetTiltakDTO target, MappingContext context) {

                        target.setTiltakstype(source.getTypeSikkerhetsTiltak());
                        target.setBeskrivelse(source.getBeskrSikkerhetsTiltak());
                        target.setGyldigFraOgMed(isNotBlank(source.getSikrFom()) ?
                                LocalDate.parse(source.getSikrFom()).atStartOfDay() : null);
                        target.setGyldigTilOgMed(isNotBlank(source.getSikrTom()) ?
                                LocalDate.parse(source.getSikrTom()).atStartOfDay() : null);
                    }
                })
                .register();
    }
}
