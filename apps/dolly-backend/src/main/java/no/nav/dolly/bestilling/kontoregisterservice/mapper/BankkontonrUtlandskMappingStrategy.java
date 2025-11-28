package no.nav.dolly.bestilling.kontoregisterservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.kontoregisterservice.util.BankkontoGenerator;
import no.nav.dolly.bestilling.kontoregisterservice.util.KontoregisterLandkode;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.UtenlandskKontoDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BankkontonrUtlandskMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BankkontonrUtlandDTO.class, OppdaterKontoRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BankkontonrUtlandDTO bestilling, OppdaterKontoRequestDTO kontoregister, MappingContext context) {

                        kontoregister.setOpprettetAv("Dolly");
                        kontoregister.setKontohaver((String)context.getProperty("ident"));

                        var kontonummer = BooleanUtils.isTrue(bestilling.getTilfeldigKontonummer()) ?
                                BankkontoGenerator.tilfeldigUtlandskBankkonto(bestilling.getLandkode()) : bestilling.getKontonummer();
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
