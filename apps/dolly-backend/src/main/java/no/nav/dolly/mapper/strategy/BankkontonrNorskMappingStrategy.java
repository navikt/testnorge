package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.kontoregisterservice.KontoregisterConsumer;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

@Component
public class BankkontonrNorskMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BankkontonrNorskDTO.class, OppdaterKontoRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrNorskDTO bestilling, OppdaterKontoRequestDTO kontoregister, MappingContext context) {
                        kontoregister.setOpprettetAv("Dolly");
                        kontoregister.setUtenlandskKonto(null);

                        var kontonummer = BooleanUtils.isTrue(bestilling.getTilfeldigKontonummer()) ?
                                KontoregisterConsumer.tilfeldigNorskBankkonto() : bestilling.getKontonummer();
                        kontoregister.setKontonummer(kontonummer);
                    }
                })
                .byDefault()
                .register();
    }
}
