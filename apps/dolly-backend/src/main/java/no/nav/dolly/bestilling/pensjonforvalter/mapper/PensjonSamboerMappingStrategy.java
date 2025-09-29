package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSivilstandWrapper;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Component
public class PensjonSamboerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PensjonSivilstandWrapper.class, List.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonSivilstandWrapper sivilstander, List list, MappingContext context) {

                        var ident = (String) context.getProperty(IDENT);
                        sivilstander.getSivilstander().stream()
                                .sorted(Comparator.comparing(SivilstandDTO::getId).reversed())
                                .filter(SivilstandDTO::isSamboer)
                                .findFirst()
                                .ifPresent(sivilstandSamboer -> {

                                    var datoTom = nonNull(sivilstandSamboer.getSivilstandsdato()) ?
                                            toLocalDate(sivilstandSamboer.getSivilstandsdato()) :
                                            LocalDate.now();
                                    var hovedperson = PensjonSamboerRequest.builder()
                                            .pidBruker(ident)
                                            .pidSamboer(sivilstandSamboer.getRelatertVedSivilstand())
                                            .datoFom(datoTom)
                                            .datoTom(sivilstander.getSivilstander().stream()
                                                    .sorted(Comparator.comparing(SivilstandDTO::getId).reversed())
                                                    .filter(sivilstand -> sivilstand.isGift() &&
                                                            sivilstander.getSivilstander().stream()
                                                                    .anyMatch(sivilstand2 -> sivilstand2.isSamboer() &&
                                                                            sivilstand2.getId() < sivilstand.getId()))
                                                    .map(sivilstand -> toLocalDate(nonNull(sivilstand.getSivilstandsdato()) ?
                                                            sivilstand.getSivilstandsdato() : sivilstand.getBekreftelsesdato()))
                                                    .filter(localDate -> nonNull(localDate) && localDate.isAfter(datoTom))
                                                    .map(dato -> dato.minusDays(1))
                                                    .findFirst()
                                                    .orElse(null))
                                            .registrertAv(CONSUMER)
                                            .build();

                                    var samboer = mapperFacade.map(hovedperson, PensjonSamboerRequest.class);
                                    samboer.setPidBruker(hovedperson.getPidSamboer());
                                    samboer.setPidSamboer(hovedperson.getPidBruker());

                                    list.addAll(List.of(hovedperson, samboer));
                                });
                    }
                })
                .register();
    }

    private static LocalDate toLocalDate(LocalDateTime localDateTime) {
        return nonNull(localDateTime) ? localDateTime.toLocalDate() : null;
    }
}
