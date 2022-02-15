package no.nav.dolly.bestilling.skjermingsregister.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.skjermingsregister.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class SkjermingsRegisterMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BestillingPersonWrapper.class, SkjermingsDataRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BestillingPersonWrapper wrapper, SkjermingsDataRequest request, MappingContext context) {

                        if (nonNull(wrapper.getPerson())) {
                            request.setPersonident(wrapper.getPerson().getIdent());
                            request.setFornavn(wrapper.getPerson().getFornavn());
                            request.setEtternavn(wrapper.getPerson().getEtternavn());
                        } else if (nonNull(wrapper.getPdlfPerson())) {
                            request.setPersonident(wrapper.getPdlfPerson().getIdent());
                            request.setFornavn(wrapper.getPdlfPerson().getNavn().stream().findFirst()
                                    .orElse(new NavnDTO()).getFornavn());
                            request.setEtternavn(wrapper.getPdlfPerson().getNavn().stream().findFirst()
                                    .orElse(new NavnDTO()).getEtternavn());
                        }
                        request.setSkjermetFra(wrapper.getSkjermetFra());
                        request.setSkjermetTil(wrapper.getSkjermetTil());
                    }
                })
                .register();
    }
}
