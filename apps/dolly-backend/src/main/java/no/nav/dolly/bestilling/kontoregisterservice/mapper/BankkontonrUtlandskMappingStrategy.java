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

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

                        var landkode = (StringUtils.length(bestilling.getLandkode()) != 3) ?
                                bestilling.getLandkode() : KontoregisterLandkode.getIso2FromIso(bestilling.getLandkode());

                        kontoregister.setUtenlandskKonto(new UtenlandskKontoDTO(
                                bestilling.getBanknavn(),
                                getBankkode(bestilling),
                                landkode,
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

    private static String getBankkode(BankkontonrUtlandDTO bestilling) {

        var tillatBankkode = KontoregisterLandkode.fraLandkode(bestilling.getLandkode())
                .filter(landkode -> nonNull(landkode.getBankkodeLengde()))
                .isPresent();

        if (!tillatBankkode) {
            return null;
        }
        return isNotBlank(bestilling.getBankkode()) ? bestilling.getBankkode() : bestilling.getIban();
    }
}
