package no.nav.dolly.bestilling.inntektsmelding.mapper;

import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilInnsendingType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class InntektsmeldingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsInntektsmelding.Inntektsmelding.class, InntektsmeldingRequest.Inntektsmelding.class)
                .customize(new CustomMapper<RsInntektsmelding.Inntektsmelding, InntektsmeldingRequest.Inntektsmelding>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.Inntektsmelding rsInntektsmelding,
                            InntektsmeldingRequest.Inntektsmelding inntektsmelding, MappingContext context) {

                        inntektsmelding.setAarsakTilInnsending(
                                nullcheckSetDefaultValue(rsInntektsmelding.getAarsakTilInnsending(), AarsakTilInnsendingType.NY));
                    }
                })
                .byDefault()
                .register();
    }
}