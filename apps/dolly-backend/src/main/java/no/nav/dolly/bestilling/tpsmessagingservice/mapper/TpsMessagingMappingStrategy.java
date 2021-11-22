package no.nav.dolly.bestilling.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.RsUtenlandskBankkonto;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.UtenlandskBankkonto;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class TpsMessagingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsUtenlandskBankkonto.class, UtenlandskBankkonto.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsUtenlandskBankkonto rsUtenlandskBankkonto, UtenlandskBankkonto utenlandskBankkonto, MappingContext context) {
                    }
                })
                .byDefault()
                .register();
    }
}
