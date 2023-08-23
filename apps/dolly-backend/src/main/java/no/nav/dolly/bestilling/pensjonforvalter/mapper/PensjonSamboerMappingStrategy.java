package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Component
public class PensjonSamboerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(SivilstandDTO.class, List.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SivilstandDTO sivilstandDTO, List list, MappingContext context) {

                        var ident = (String) context.getProperty("ident");
                        var hovedperson = PensjonSamboerRequest.builder()
                                .pidBruker(ident)
                                .pidSamboer(sivilstandDTO.getRelatertVedSivilstand())
                                .datoFom(toLocalDate(sivilstandDTO.getSivilstandsdato()))
                                .registrertAv(CONSUMER)
                                .build();

                        var samboer = mapperFacade.map(hovedperson, PensjonSamboerRequest.class);
                        samboer.setPidBruker(hovedperson.getPidSamboer());
                        samboer.setPidSamboer(hovedperson.getPidBruker());

                        list.add(hovedperson);
                        list.add(samboer);
                    }
                })
                .register();
    }

    private static LocalDate toLocalDate(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.toLocalDate() : null;
    }
}
