package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.BankkontoUtlandRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@Component
public class BankkontonrUtlandMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BankkontonrUtlandDTO.class, BankkontoUtlandRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrUtlandDTO source, BankkontoUtlandRequest target, MappingContext context) {

                        target.setSfeAjourforing(BankkontoUtlandRequest.SfeAjourforing.builder()
                                .systemInfo(TpsSystemInfo.builder()
                                        .kilde("Dolly")
                                        .brukerID("anonymousUser")
                                        .build())
                                .endreGironrUtl(mapperFacade.map(source, BankkontoUtlandRequest.EndreGironrUtl.class, context))
                                .build());
                    }
                })
                .register();

        factory.classMap(BankkontonrUtlandDTO.class, BankkontoUtlandRequest.EndreGironrUtl.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrUtlandDTO source, BankkontoUtlandRequest.EndreGironrUtl target, MappingContext context) {

                        target.setOffentligIdent((String) context.getProperty("ident"));
                        target.setDatoGiroNr((isNull(source.getDatoGiroNr()) ? LocalDate.now() : source.getDatoGiroNr()).toString());
                    }
                })
                .byDefault()
                .register();
    }
}
