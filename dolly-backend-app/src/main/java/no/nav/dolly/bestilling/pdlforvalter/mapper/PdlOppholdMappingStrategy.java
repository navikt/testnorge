package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold.getOppholdType;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlOppholdMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(RsUdiPerson.class, PdlOpphold.class)
                .customize(new CustomMapper<RsUdiPerson, PdlOpphold>() {
                    @Override
                    public void mapAtoB(RsUdiPerson person, PdlOpphold opphold, MappingContext context) {

                        if (nonNull(person.getOppholdStatus())) {
                            opphold.setType(
                                    getOppholdType(person.getOppholdStatus().getEosEllerEFTABeslutningOmOppholdsrett()));

                            if (nonNull(person.getOppholdStatus().getEosEllerEFTAOppholdstillatelsePeriode())) {
                                opphold.setOppholdFra(
                                        toLocalDate(person.getOppholdStatus().getEosEllerEFTAOppholdstillatelsePeriode().getFra()));
                                opphold.setOppholdTil(
                                        toLocalDate(person.getOppholdStatus().getEosEllerEFTAOppholdstillatelsePeriode().getTil()));
                            }
                        }
                        opphold.setKilde(CONSUMER);
                    }
                }).register();
    }

    private static LocalDate toLocalDate(LocalDateTime timestamp) {
        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }
}
