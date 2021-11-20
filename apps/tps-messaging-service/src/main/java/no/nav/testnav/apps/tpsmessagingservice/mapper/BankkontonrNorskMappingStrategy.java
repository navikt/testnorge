package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.BankkontoNorskRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@Component
public class BankkontonrNorskMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BankkontonrNorskDTO.class, BankkontoNorskRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrNorskDTO source, BankkontoNorskRequest target, MappingContext context) {

                        target.setSfeAjourforing(BankkontoNorskRequest.SfeAjourforing.builder()
                                .systemInfo(TpsSystemInfo.builder()
                                        .kilde("Dolly")
                                        .brukerID("anonymousUser")
                                        .build())
                                .endreGironrNorsk(mapperFacade.map(source, BankkontoNorskRequest.EndreGironrNorsk.class, context))
                                .build());
                    }
                })
                .register();

        factory.classMap(BankkontonrNorskDTO.class, BankkontoNorskRequest.EndreGironrNorsk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrNorskDTO source, BankkontoNorskRequest.EndreGironrNorsk target, MappingContext context) {

                        target.setOffentligIdent((String) context.getProperty("ident"));
                        target.setDatoGiroNrNorsk((isNull(source.getDatoGiroNrNorsk()) ? LocalDate.now() : source.getDatoGiroNrNorsk()).toString());
                    }
                })
                .byDefault()
                .register();
    }
}
