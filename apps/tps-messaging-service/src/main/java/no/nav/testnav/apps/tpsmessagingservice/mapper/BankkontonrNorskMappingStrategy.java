package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.BankkontoNorskRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.tps.ctg.s610.domain.BankkontoNorgeType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class BankkontonrNorskMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BankkontonrNorskDTO.class, BankkontoNorskRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrNorskDTO source, BankkontoNorskRequest target, MappingContext context) {

                        target.setSfeAjourforing(BankkontoNorskRequest.SfeAjourforing.builder()
                                .systemInfo(TpsSystemInfo.getDefault())
                                .endreGironrNorsk(isNotBlank(source.getKontonummer()) ?
                                        mapperFacade.map(source, BankkontoNorskRequest.EndreGironrNorsk.class, context) :
                                        null)
                                .opphorGironrNorsk(isBlank(source.getKontonummer()) ?
                                        mapperFacade.map(source, BankkontoNorskRequest.BrukerIdentifikasjon.class, context) :
                                        null)
                                .build());
                    }
                })
                .register();

        factory.classMap(BankkontonrNorskDTO.class, BankkontoNorskRequest.EndreGironrNorsk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrNorskDTO source, BankkontoNorskRequest.EndreGironrNorsk target, MappingContext context) {

                        target.setOffentligIdent((String) context.getProperty("ident"));
                        target.setGiroNrNorsk(source.getKontonummer());
                        target.setDatoGiroNrNorsk((isNull(source.getKontoRegdato()) ? LocalDate.now() :
                                source.getKontoRegdato().toLocalDate()).toString());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(BankkontonrNorskDTO.class, BankkontoNorskRequest.BrukerIdentifikasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrNorskDTO source, BankkontoNorskRequest.BrukerIdentifikasjon target, MappingContext context) {

                        target.setOffentligIdent((String) context.getProperty("ident"));
                    }
                })
                .register();

        factory.classMap(BankkontoNorgeType.class, BankkontonrNorskDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontoNorgeType source, BankkontonrNorskDTO target, MappingContext context) {

                        target.setKontonummer(isBlank(source.getKontoNummer()) ? null :
                                "%s.%s.%s".formatted(source.getKontoNummer().substring(0, 4),
                                        source.getKontoNummer().substring(4, 6), source.getKontoNummer().substring(6)));

                        target.setKontoRegdato(isBlank(source.getRegTidspunkt()) ? null :
                                LocalDate.parse(source.getRegTidspunkt()).atStartOfDay());
                    }
                })
                .register();
    }
}
