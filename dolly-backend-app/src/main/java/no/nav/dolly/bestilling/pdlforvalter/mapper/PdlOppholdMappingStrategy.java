package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold.OppholdType;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdsrettType;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdstillatelse;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdstillatelseType;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiVarighetOpphold;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlOppholdMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(RsUdiPerson.class, PdlOpphold.class)
                .customize(new CustomMapper<RsUdiPerson, PdlOpphold>() {
                    @Override
                    public void mapAtoB(RsUdiPerson person, PdlOpphold opphold, MappingContext context) {

                        if (nonNull(person.getOppholdStatus())) {

                            if (nonNull(person.getOppholdStatus().getEosEllerEFTABeslutningOmOppholdsrettPeriode())) {
                                opphold.setType(
                                        getOppholdType(person.getOppholdStatus().getEosEllerEFTABeslutningOmOppholdsrett()));
                                opphold.setOppholdFra(
                                        toLocalDate(person.getOppholdStatus().getEosEllerEFTABeslutningOmOppholdsrettPeriode().getFra()));
                                opphold.setOppholdTil(
                                        toLocalDate(person.getOppholdStatus().getEosEllerEFTABeslutningOmOppholdsrettPeriode().getTil()));

                            } else if (nonNull(person.getOppholdStatus().getEosEllerEFTAOppholdstillatelsePeriode())) {
                                opphold.setType(
                                        getOppholdType(person.getOppholdStatus().getEosEllerEFTAOppholdstillatelse()));
                                opphold.setOppholdFra(
                                        toLocalDate(person.getOppholdStatus().getEosEllerEFTAOppholdstillatelsePeriode().getFra()));
                                opphold.setOppholdTil(
                                        toLocalDate(person.getOppholdStatus().getEosEllerEFTAOppholdstillatelsePeriode().getTil()));

                            } else if (nonNull(person.getOppholdStatus().getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode())) {
                                opphold.setType(
                                        getOppholdType(person.getOppholdStatus().getEosEllerEFTAVedtakOmVarigOppholdsrett()));
                                opphold.setOppholdFra(
                                        toLocalDate(person.getOppholdStatus().getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode().getFra()));
                                opphold.setOppholdTil(
                                        toLocalDate(person.getOppholdStatus().getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode().getTil()));

                            } else if (nonNull(person.getOppholdStatus().getOppholdSammeVilkaar())) {
                                opphold.setType(
                                        getOppholdType(person.getOppholdStatus().getOppholdSammeVilkaar().getOppholdstillatelseType()));
                                if (nonNull(person.getOppholdStatus().getOppholdSammeVilkaar().getOppholdSammeVilkaarPeriode())) {
                                    opphold.setOppholdFra(
                                            toLocalDate(person.getOppholdStatus().getOppholdSammeVilkaar().getOppholdSammeVilkaarPeriode().getFra()));
                                    opphold.setOppholdTil(
                                            toLocalDate(person.getOppholdStatus().getOppholdSammeVilkaar().getOppholdSammeVilkaarPeriode().getTil()));
                                }
                            }
                        }
                        opphold.setKilde(CONSUMER);
                    }
                }).register();
    }

    private static LocalDate toLocalDate(LocalDateTime timestamp) {
        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    public static OppholdType getOppholdType(UdiOppholdsrettType oppholdsrettType) {

        if (isNull(oppholdsrettType)) {
            return OppholdType.OPPLYSNING_MANGLER;
        }
        switch (oppholdsrettType) {
        case FAMILIE:
        case VARIG:
            return OppholdType.PERMANENT;
        case TJENESTEYTING_ELLER_ETABLERING:
            return OppholdType.MIDLERTIDIG;
        case UAVKLART:
        case INGEN_INFORMASJON:
        default:
            return OppholdType.OPPLYSNING_MANGLER;
        }
    }

    public static OppholdType getOppholdType(UdiOppholdstillatelse oppholdstillatelse) {

        if (isNull(oppholdstillatelse)) {
            return OppholdType.OPPLYSNING_MANGLER;
        }

        switch (oppholdstillatelse) {
        case FAMILIE:
            return OppholdType.PERMANENT;
        case ARBEID:
        case EGNE_MIDLER_ELLER_FASTE_PERIODISKE_YTELSER:
        case TJENESTEYTING_ELLER_ETABLERING:
        case UTDANNING:
            return OppholdType.MIDLERTIDIG;
        case UAVKLART:
        default:
            return OppholdType.OPPLYSNING_MANGLER;
        }
    }

    public static OppholdType getOppholdType(UdiVarighetOpphold varigOpphold) {

        if (isNull(varigOpphold)) {
            return OppholdType.OPPLYSNING_MANGLER;
        }
        switch (varigOpphold) {
        case FAMILIE:
        case VARIG:
            return OppholdType.PERMANENT;
        case TJENESTEYTING_ELLER_ETABLERING:
            return OppholdType.MIDLERTIDIG;
        case UAVKLART:
        case INGEN_INFORMASJON:
        default:
            return OppholdType.OPPLYSNING_MANGLER;
        }
    }

    public static OppholdType getOppholdType(UdiOppholdstillatelseType oppholdstillatelseType) {

        if (isNull(oppholdstillatelseType)) {
            return OppholdType.OPPLYSNING_MANGLER;
        }
        switch (oppholdstillatelseType) {
        case PERMANENT:
            return OppholdType.PERMANENT;
        case MIDLERTIDIG:
        default:
            return OppholdType.MIDLERTIDIG;
        }
    }
}
