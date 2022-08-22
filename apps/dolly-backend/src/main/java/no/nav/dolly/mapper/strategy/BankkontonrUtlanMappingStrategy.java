package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.UtenlandskKontoDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import org.springframework.stereotype.Component;

@Component
public class BankkontonrUtlanMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BankkontonrUtlandDTO.class, OppdaterKontoRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrUtlandDTO bestilling, OppdaterKontoRequestDTO kontoregister, MappingContext context) {
                        kontoregister.setKontonummer(bestilling.getKontonummer());
                        kontoregister.setOpprettetAv("Dolly");

                        kontoregister.setUtenlandskKonto(new UtenlandskKontoDTO(
                                bestilling.getBanknavn(),
                                "", // bankkode mangler i BankkontonrUtlandDTO
                                bestilling.getLandkode(),
                                bestilling.getValuta(),
                                bestilling.getSwift(),
                                bestilling.getBankAdresse1(),
                                bestilling.getBankAdresse2(),
                                bestilling.getBankAdresse3()
                        ));
                    }
                })
                .byDefault()
                .register();
    }
}
