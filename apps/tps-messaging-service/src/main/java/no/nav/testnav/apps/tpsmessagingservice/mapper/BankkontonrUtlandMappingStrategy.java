package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.BankkontoUtlandRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.tps.ctg.s610.domain.KontoNrUtlandType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

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
                        target.setDatoGiroNr((isNull(source.getKontoRegdato()) ? LocalDate.now() :
                                source.getKontoRegdato().toLocalDate()).toString());
                        target.setKodeBank(source.getIban());
                        target.setGiroNrUtland(source.getKontonummer());
                        target.setKodeLand(source.getLandkode());
                        target.setKodeSwift(source.getSwift());
                        target.setBankNavn(source.getBanknavn());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(KontoNrUtlandType.class, BankkontonrUtlandDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(KontoNrUtlandType source, BankkontonrUtlandDTO target, MappingContext context) {

                        target.setKontonummer(source.getGiroNrUtland());
                        target.setKontoRegdato(isBlank(source.getRegTidspunkt()) ? null :
                                LocalDate.parse(source.getRegTidspunkt()).atStartOfDay());
                        target.setBanknavn(source.getBankNavnUtland());
                        target.setSwift(source.getSwiftKodeUtland());
                        target.setLandkode(source.getBankLandKode());
                        target.setValuta(source.getBankValuta());
                    }
                })
                .byDefault()
                .register();
    }
}