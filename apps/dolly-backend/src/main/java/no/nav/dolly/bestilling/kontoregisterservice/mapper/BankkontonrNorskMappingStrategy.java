package no.nav.dolly.bestilling.kontoregisterservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.kontoregisterservice.util.BankkontoGenerator;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
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
                        kontoregister.setKontohaver((String)context.getProperty("ident"));
                        kontoregister.setUtenlandskKonto(null);

                        var kontonummer = BooleanUtils.isTrue(bestilling.getTilfeldigKontonummer()) ?
                                BankkontoGenerator.tilfeldigNorskBankkonto() : bestilling.getKontonummer();
                        kontoregister.setKontonummer(kontonummer);
                    }
                })
                .byDefault()
                .register();
    }
}
