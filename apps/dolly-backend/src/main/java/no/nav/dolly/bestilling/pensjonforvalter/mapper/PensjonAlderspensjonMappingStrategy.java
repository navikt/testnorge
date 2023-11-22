package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.sykemelding.Norg2Consumer;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getNesteMaaned;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getRandomAnsatt;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class PensjonAlderspensjonMappingStrategy implements MappingStrategy {

    private static Norg2Consumer norg2Consumer;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PensjonData.Alderspensjon.class, AlderspensjonRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.Alderspensjon alderspensjon, AlderspensjonRequest request, MappingContext context) {

                        request.setFnr((String) context.getProperty("ident"));
                        request.setMiljoer((List<String>) context.getProperty("miljoer"));

                        request.setKravFremsattDato(
                                nullcheckSetDefaultValue(alderspensjon.getKravFremsattDato(), LocalDate.now()));
                        request.setOnsketVirkningsDato(
                                nullcheckSetDefaultValue(alderspensjon.getIverksettelsesdato(), getNesteMaaned()));

                        if (isBlank(request.getNavEnhetId())) {
                            norg2Consumer.getNorgEnhet()
                        }

                        request.setSaksbehandler(nullcheckSetDefaultValue(alderspensjon.getSaksbehandler(), getRandomAnsatt()));
                        request.setAttesterer(nullcheckSetDefaultValue(alderspensjon.getAttesterer(), getRandomAnsatt()));
                    }
                })
                .byDefault()
                .register();
    }
}