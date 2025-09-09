package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.RevurderingVedtakRequest;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getNesteMaaned;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getRandomAnsatt;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class PensjonAlederspensjonRevurderingVedtakMappingStrategy implements MappingStrategy {

    private static final String NAV_ENHET = "navEnhetId";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(AlderspensjonVedtakRequest.class, RevurderingVedtakRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(AlderspensjonVedtakRequest alderspensjonVedtakRequest, RevurderingVedtakRequest revurderingVedtakRequest, MappingContext context) {

                        revurderingVedtakRequest.setFom(getNesteMaaned());
                        revurderingVedtakRequest.setRevurderingArsakType("SIVILSTANDENDRING");
                        revurderingVedtakRequest.setNavEnhetId(isNotBlank(alderspensjonVedtakRequest.getNavEnhetId()) ?
                                alderspensjonVedtakRequest.getNavEnhetId() : ((String) context.getProperty(NAV_ENHET)));
                        revurderingVedtakRequest.setSaksbehandler(isNotBlank(alderspensjonVedtakRequest.getSaksbehandler()) ?
                                alderspensjonVedtakRequest.getSaksbehandler() : getRandomAnsatt());
                        revurderingVedtakRequest.setAttesterer(isNotBlank(alderspensjonVedtakRequest.getAttesterer()) ?
                                alderspensjonVedtakRequest.getAttesterer() : getRandomAnsatt());
                    }
                })
                .byDefault()
                .register();
    }
}
