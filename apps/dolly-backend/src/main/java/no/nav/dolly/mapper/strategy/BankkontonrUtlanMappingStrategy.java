package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.kontoregisterservice.KontoregisterConsumer;
import no.nav.dolly.bestilling.tpsmessagingservice.KontoregisterLandkode;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.UtenlandskKontoDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrUtlandDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BankkontonrUtlanMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BankkontonrUtlandDTO.class, OppdaterKontoRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrUtlandDTO bestilling, OppdaterKontoRequestDTO kontoregister, MappingContext context) {
                        kontoregister.setOpprettetAv("Dolly");

                        var kontonummer = BooleanUtils.isTrue(bestilling.getTilfeldigKontonummer()) ?
                                KontoregisterConsumer.tilfeldigUtlandskBankkonto(bestilling.getLandkode()) : bestilling.getKontonummer();
                        kontoregister.setKontonummer(kontonummer);

                        kontoregister.setUtenlandskKonto(new UtenlandskKontoDTO(
                                bestilling.getBanknavn(),
                                "", // bankkode mangler i BankkontonrUtlandDTO
                                (StringUtils.length(bestilling.getLandkode()) != 3) ?
                                        bestilling.getLandkode() : KontoregisterLandkode.getIso2FromIso(bestilling.getLandkode()),
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
