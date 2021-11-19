package no.nav.dolly.bestilling.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.KontaktOpplysninger;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.KontaktOpplysninger.UtenlandskBankkonto;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class OpprettKontaktinformasjonMapper implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsTpsMessaging.class, KontaktOpplysninger.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsTpsMessaging rsTpsMessaging, KontaktOpplysninger kontaktOpplysninger, MappingContext context) {
                        KontaktOpplysninger.builder()
                                .endringAvkontonr(KontaktOpplysninger.EndringKontonummer.builder()
                                        .endreKontonrUtland(!rsTpsMessaging.getUtenlandskBankkonto().isEmpty()
                                                ? mapperFacade.map(rsTpsMessaging.getUtenlandskBankkonto().get(0), UtenlandskBankkonto.class)
                                                : null)
                                        .build())
                                .build();

                    }
                })
                .byDefault()
                .register();
    }
}
