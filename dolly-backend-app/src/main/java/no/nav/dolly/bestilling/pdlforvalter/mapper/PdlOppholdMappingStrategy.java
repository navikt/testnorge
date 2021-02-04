package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold.OppholdType;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.opphold.RsUdiOppholdStatus;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdsrettType;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdstillatelse;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdstillatelseType;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiVarighetOpphold;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Component
public class PdlOppholdMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(RsUdiPerson.class, PdlOpphold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsUdiPerson person, PdlOpphold opphold, MappingContext context) {

                        if (nonNull(person.getOppholdStatus())) {
                            opphold.setType(OppholdType.OPPLYSNING_MANGLER);

                            if (nonNull(person.getOppholdStatus().getEosEllerEFTABeslutningOmOppholdsrett())) {
                                copyBeslutningOmOppholdsrett(person.getOppholdStatus(), opphold);

                            } else if (nonNull(person.getOppholdStatus().getEosEllerEFTAOppholdstillatelse())) {
                                copyOppholdstilatelse(person.getOppholdStatus(), opphold);

                            } else if (nonNull(person.getOppholdStatus().getEosEllerEFTAVedtakOmVarigOppholdsrett())) {
                                copyVedtakOmVarigOppholdsrett(person.getOppholdStatus(), opphold);

                            } else if (nonNull(person.getOppholdStatus().getOppholdSammeVilkaar())) {
                                copyOppholdSammeVilkaar(person.getOppholdStatus(), opphold);
                            }
                        }
                        opphold.setKilde(CONSUMER);
                    }
                }).register();
    }

    private static void copyOppholdSammeVilkaar(RsUdiOppholdStatus oppholdStatus, PdlOpphold opphold) {

        opphold.setType(getOppholdType(oppholdStatus.getOppholdSammeVilkaar().getOppholdstillatelseType()));
        if (nonNull(oppholdStatus.getOppholdSammeVilkaar().getOppholdSammeVilkaarPeriode())) {
            opphold.setOppholdFra(
                    toLocalDate(oppholdStatus.getOppholdSammeVilkaar().getOppholdSammeVilkaarPeriode().getFra()));
            opphold.setOppholdTil(
                    toLocalDate(oppholdStatus.getOppholdSammeVilkaar().getOppholdSammeVilkaarPeriode().getTil()));
        }
    }

    private static void copyVedtakOmVarigOppholdsrett(RsUdiOppholdStatus oppholdStatus, PdlOpphold opphold) {

        opphold.setType(getOppholdType(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrett()));
        if (nonNull(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode())) {
            opphold.setOppholdFra(
                    toLocalDate(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode().getFra()));
            opphold.setOppholdTil(
                    toLocalDate(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode().getTil()));
        }
    }

    private static void copyOppholdstilatelse(RsUdiOppholdStatus oppholdStatus, PdlOpphold opphold) {

        opphold.setType(getOppholdType(oppholdStatus.getEosEllerEFTAOppholdstillatelse()));
        if (nonNull(oppholdStatus.getEosEllerEFTAOppholdstillatelsePeriode())) {
            opphold.setOppholdFra(
                    toLocalDate(oppholdStatus.getEosEllerEFTAOppholdstillatelsePeriode().getFra()));
            opphold.setOppholdTil(
                    toLocalDate(oppholdStatus.getEosEllerEFTAOppholdstillatelsePeriode().getTil()));
        }
    }

    private static void copyBeslutningOmOppholdsrett(RsUdiOppholdStatus oppholdStatus, PdlOpphold opphold) {

        opphold.setType(getOppholdType(oppholdStatus.getEosEllerEFTABeslutningOmOppholdsrett()));
        if (nonNull(oppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode())) {
            opphold.setOppholdFra(
                    toLocalDate(oppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode().getFra()));
            opphold.setOppholdTil(
                    toLocalDate(oppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode().getTil()));
        }
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
