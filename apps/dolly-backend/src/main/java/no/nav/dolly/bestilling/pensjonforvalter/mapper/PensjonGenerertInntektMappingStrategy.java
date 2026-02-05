package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import lombok.val;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppGenerertInntektRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PensjonGenerertInntektMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PensjonData.PoppGenerertInntektWrapper.class, PensjonPoppGenerertInntektRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.PoppGenerertInntektWrapper generertInntektWrapper, PensjonPoppGenerertInntektRequest request, MappingContext context) {

                        val ident = (String) context.getProperty("ident");
                        val miljoe = (String) context.getProperty("miljoe");

                        request.setFnr(ident);
                        request.setMiljoer(List.of(miljoe));
                    }
                })
                .byDefault()
                .register();
    }
}