package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonNyUtaksgradRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getRandomAnsatt;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.NAV_ENHET;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class PensjonAlderspensjonNyUttaksgradMappingStrategy implements MappingStrategy {

    private static final String ALDERSPENSJON_VEDTAK = "AlderspensjonVedtak";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PensjonData.AlderspensjonNyUtaksgrad.class, AlderspensjonNyUtaksgradRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.AlderspensjonNyUtaksgrad alderspensjonVedtakRequest,
                                        AlderspensjonNyUtaksgradRequest nyUtaksgradRequest, MappingContext context) {

                        var vedtak = (AlderspensjonVedtakRequest) context.getProperty(ALDERSPENSJON_VEDTAK);

                        nyUtaksgradRequest.setFnr(vedtak.getFnr());
                        nyUtaksgradRequest.setMiljoer(vedtak.getMiljoer());

                        if (StringUtils.isBlank(nyUtaksgradRequest.getAttesterer())) {
                            nyUtaksgradRequest.setAttesterer(isNotBlank(vedtak.getAttesterer())?
                                    vedtak.getAttesterer() : getRandomAnsatt());
                        }
                        if (StringUtils.isBlank(nyUtaksgradRequest.getSaksbehandler())) {
                            nyUtaksgradRequest.setSaksbehandler(isNotBlank(vedtak.getSaksbehandler())?
                                    vedtak.getSaksbehandler() : getRandomAnsatt());
                        }
                        if (StringUtils.isBlank(nyUtaksgradRequest.getNavEnhetId())) {
                            nyUtaksgradRequest.setNavEnhetId(isNotBlank(vedtak.getNavEnhetId())?
                                    vedtak.getNavEnhetId() : (String) context.getProperty(NAV_ENHET));
                        }

                        var fom = nonNull(alderspensjonVedtakRequest.getFom()) ?
                                alderspensjonVedtakRequest.getFom() : vedtak.getIverksettelsesdato();

                        nyUtaksgradRequest.setFom(getNesteMaaned(fom));
                    }
                })
                .byDefault()
                .register();
    }

    private static LocalDate getNesteMaaned(LocalDate date) {

        if (isNull(date)) {
            return PensjonMappingSupportUtils.getNesteMaaned();

        } else if (date.getDayOfMonth() == 1) {
            return date;

        } else {
            return PensjonMappingSupportUtils.getNesteMaaned(date);
        }
    }
}