package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.SikkerhetstiltakRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SikkerhetTiltakDTO;
import no.nav.tps.ctg.s610.domain.TsikkerhetsTiltakS610;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class SikkerhetstiltakMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SikkerhetTiltakDTO.class, SikkerhetstiltakRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SikkerhetTiltakDTO source, SikkerhetstiltakRequest target, MappingContext context) {

                        target.setSfeAjourforing(SikkerhetstiltakRequest.SfeAjourforing.builder()
                                .systemInfo(TpsSystemInfo.getDefault())
                                .opprettSikkerhetsTiltak(mapperFacade.map(source, SikkerhetstiltakRequest.Sikkerhetstiltak.class, context))
                                .opphorSikkerhetsTiltak(mapperFacade.map(source, SikkerhetstiltakRequest.BrukerIdentifikasjon.class, context))
                                .build());
                    }
                })
                .register();

        factory.classMap(SikkerhetTiltakDTO.class, SikkerhetstiltakRequest.Sikkerhetstiltak.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SikkerhetTiltakDTO source, SikkerhetstiltakRequest.Sikkerhetstiltak target, MappingContext context) {

                        target.setTypeSikkerhetsTiltak(source.getTiltakstype());
                        target.setBeskrSikkerhetsTiltak(source.getBeskrivelse());
                        target.setFom(nonNull(source.getGyldigFraOgMed()) ? source.getGyldigFraOgMed().toLocalDate().toString() : null);
                        target.setTom(nonNull(source.getGyldigTilOgMed()) ? source.getGyldigTilOgMed().toLocalDate().toString() : null);
                    }
                })
                .register();

        factory.classMap(SikkerhetTiltakDTO.class, SikkerhetstiltakRequest.BrukerIdentifikasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SikkerhetTiltakDTO source, SikkerhetstiltakRequest.BrukerIdentifikasjon target, MappingContext context) {

                        target.setOffentligIdent((String) context.getProperty("ident"));
                    }
                })
                .register();

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
