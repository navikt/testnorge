package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getNesteMaaned;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getRandomAnsatt;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@Component
@RequiredArgsConstructor
public class PensjonAlderspensjonVedtakMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PensjonData.Alderspensjon.class, AlderspensjonVedtakRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.Alderspensjon alderspensjon, AlderspensjonVedtakRequest request, MappingContext context) {

                        request.setFnr((String) context.getProperty("ident"));
                        request.setMiljoer((List<String>) context.getProperty("miljoer"));

                        request.setKravFremsattDato(
                                nullcheckSetDefaultValue(alderspensjon.getKravFremsattDato(), LocalDate.now()));
                        request.setIverksettelsesdato(
                                nullcheckSetDefaultValue(alderspensjon.getIverksettelsesdato(), getNesteMaaned()));

                        request.setNavEnhetId(
                                nullcheckSetDefaultValue(alderspensjon.getNavEnhetId(),
                                        (String) context.getProperty("navEnhet")));

                        request.setSaksbehandler(nullcheckSetDefaultValue(alderspensjon.getSaksbehandler(), getRandomAnsatt()));
                        request.setAttesterer(nullcheckSetDefaultValue(alderspensjon.getAttesterer(), getRandomAnsatt()));
                    }
                })
                .byDefault()
                .register();
    }
}