package no.nav.dolly.bestilling.skjermingsregister.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.skjermingsregister.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SkjermingsRegisterMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BestillingPersonWrapper.class, SkjermingsDataRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BestillingPersonWrapper wrapper, SkjermingsDataRequest request, MappingContext context) {

                        request.setPersonident(wrapper.getPerson().getIdent());
                        request.setFornavn(wrapper.getPerson().getFornavn());
                        request.setEtternavn(wrapper.getPerson().getEtternavn());
                        request.setSkjermetFra(wrapper.getSkjermetFra());
                        request.setSkjermetTil(wrapper.getSkjermetTil());
                    }
                })
                .register();
    }
}
