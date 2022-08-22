package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import org.springframework.stereotype.Component;

@Component
public class BankkontonrNorskMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BankkontonrNorskDTO.class, OppdaterKontoRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrNorskDTO bestilling, OppdaterKontoRequestDTO kontoregister, MappingContext context) {
                        kontoregister.setKontonummer(bestilling.getKontonummer());
                        kontoregister.setOpprettetAv("Dolly");
                        kontoregister.setUtenlandskKonto(null);
                    }
                })
                .byDefault()
                .register();
    }
}
