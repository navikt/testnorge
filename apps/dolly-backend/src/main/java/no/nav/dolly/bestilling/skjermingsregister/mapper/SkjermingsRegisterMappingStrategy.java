package no.nav.dolly.bestilling.skjermingsregister.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingBestilling;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SkjermingsRegisterMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SkjermingBestilling.class, SkjermingDataRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkjermingBestilling bestilling, SkjermingDataRequest request, MappingContext context) {

                            var personBolk = (PdlPersonBolk.PersonBolk) context.getProperty("personBolk");
                            request.setPersonident(personBolk.getIdent());
                            request.setFornavn(personBolk.getPerson().getNavn().stream().map(PdlPerson.Navn::getFornavn)
                                    .findFirst().orElse(null));
                            request.setEtternavn(personBolk.getPerson().getNavn().stream().map(PdlPerson.Navn::getEtternavn)
                                    .findFirst().orElse(null));
                    }
                })
                .byDefault()
                .register();
    }
}
