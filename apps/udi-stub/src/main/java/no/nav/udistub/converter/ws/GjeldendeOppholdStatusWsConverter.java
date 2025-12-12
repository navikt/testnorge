package no.nav.udistub.converter.ws;

import lombok.RequiredArgsConstructor;
import no.nav.udistub.service.dto.opphold.UdiOppholdSammeVilkaar;
import no.nav.udistub.service.dto.opphold.UdiOppholdStatus;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTABeslutningOmOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAOpphold;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAVedtakOmVarigOppholdsrett;
import no.udi.mt_1067_nav_data.v1.GjeldendeOppholdsstatus;
import no.udi.mt_1067_nav_data.v1.Oppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.OppholdstillatelseEllerOppholdsPaSammeVilkar;
import no.udi.mt_1067_nav_data.v1.Uavklart;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
public class GjeldendeOppholdStatusWsConverter implements Converter<UdiOppholdStatus, GjeldendeOppholdsstatus> {

    private final XmlDateWsConverter xmlDateWsConverter;
    private final PeriodeWsConverter periodeWsConverter;
    private final IkkeOppholdstillatelseIkkeOppholdPaSammeVilkarIkkeVisumWsConverter ikkeWsConverter;

    @Override
    public GjeldendeOppholdsstatus convert(UdiOppholdStatus oppholdStatus) {
        if (isNull(oppholdStatus)) {
            return null;
        }

        var resultatOppholdsstatus = new GjeldendeOppholdsstatus();

        resultatOppholdsstatus.setUavklart(isTrue(oppholdStatus.getUavklart()) ? new Uavklart() : null);

        var eoSellerEFTAOpphold = new EOSellerEFTAOpphold();
        eoSellerEFTAOpphold.setEOSellerEFTABeslutningOmOppholdsrett(getEOSellerEFTABeslutningOmOpphold(oppholdStatus));
        eoSellerEFTAOpphold.setEOSellerEFTAVedtakOmVarigOppholdsrett(getEOSellerEFTAVedtakOmVarigOppholdrett(oppholdStatus));
        eoSellerEFTAOpphold.setEOSellerEFTAOppholdstillatelse(getEoSellerEFTAOppholdstillatelse(oppholdStatus));

        if (nonNull(eoSellerEFTAOpphold.getEOSellerEFTAOppholdstillatelse()) ||
                nonNull(eoSellerEFTAOpphold.getEOSellerEFTABeslutningOmOppholdsrett()) ||
                nonNull(eoSellerEFTAOpphold.getEOSellerEFTAVedtakOmVarigOppholdsrett())) {
            resultatOppholdsstatus.setEOSellerEFTAOpphold(eoSellerEFTAOpphold);
        }

        if (nonNull(oppholdStatus.getOppholdSammeVilkaar())) {
            resultatOppholdsstatus.setOppholdstillatelseEllerOppholdsPaSammeVilkar(getOppholdstillatelseEllerOppholdsPaSammeVilkar(oppholdStatus.getOppholdSammeVilkaar()));
        }

        if (nonNull(oppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum())) {
            resultatOppholdsstatus.setIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(
                    ikkeWsConverter.convert(oppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum()));
        }

        return resultatOppholdsstatus;
    }

    private EOSellerEFTABeslutningOmOppholdsrett getEOSellerEFTABeslutningOmOpphold(UdiOppholdStatus oppholdsStatus) {

        if (isNull(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrettEffektuering()) &&
                isNull(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrett()) &&
                isNull(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode())) {
            return null;
        }

        var eoSellerEFTABeslutningOmOppholdsrett = new EOSellerEFTABeslutningOmOppholdsrett();
        eoSellerEFTABeslutningOmOppholdsrett.setEffektueringsdato(
                xmlDateWsConverter.convert(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrettEffektuering()));
        eoSellerEFTABeslutningOmOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrett());
        eoSellerEFTABeslutningOmOppholdsrett.setOppholdsrettsPeriode(
                periodeWsConverter.convert(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode()));
        return eoSellerEFTABeslutningOmOppholdsrett;
    }

    private EOSellerEFTAVedtakOmVarigOppholdsrett getEOSellerEFTAVedtakOmVarigOppholdrett(UdiOppholdStatus oppholdsStatus) {

        if (isNull(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering()) &&
                isNull(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrett()) &&
                isNull(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode())) {
            return null;
        }

        var eoSellerEFTAVedtakOmVarigOppholdsrett = new EOSellerEFTAVedtakOmVarigOppholdsrett();
        eoSellerEFTAVedtakOmVarigOppholdsrett.setEffektueringsdato(
                xmlDateWsConverter.convert(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering()));
        eoSellerEFTAVedtakOmVarigOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrett());
        eoSellerEFTAVedtakOmVarigOppholdsrett.setOppholdsrettsPeriode(
                periodeWsConverter.convert(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode()));

        return eoSellerEFTAVedtakOmVarigOppholdsrett;
    }

    private EOSellerEFTAOppholdstillatelse getEoSellerEFTAOppholdstillatelse(UdiOppholdStatus oppholdStatus) {

        if (isNull(oppholdStatus.getEosEllerEFTAOppholdstillatelseEffektuering()) &&
                isNull(oppholdStatus.getEosEllerEFTAOppholdstillatelse()) &&
                isNull(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode())) {
            return null;

        }
        var eoSellerEFTAOppholdstillatelse = new EOSellerEFTAOppholdstillatelse();
        eoSellerEFTAOppholdstillatelse.setEffektueringsdato(
                xmlDateWsConverter.convert(oppholdStatus.getEosEllerEFTAOppholdstillatelseEffektuering()));
        eoSellerEFTAOppholdstillatelse.setEOSOppholdsgrunnlag(oppholdStatus.getEosEllerEFTAOppholdstillatelse());
        eoSellerEFTAOppholdstillatelse.setOppholdstillatelsePeriode(
                periodeWsConverter.convert(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode()));

        return eoSellerEFTAOppholdstillatelse;
    }

    private OppholdstillatelseEllerOppholdsPaSammeVilkar getOppholdstillatelseEllerOppholdsPaSammeVilkar(UdiOppholdSammeVilkaar oppholdSammeVilkaar) {

        if (isNull(oppholdSammeVilkaar)) {
            return null;
        }

        var oppholdstillatelseEllerOppholdsPaSammeVilkar = new OppholdstillatelseEllerOppholdsPaSammeVilkar();
        oppholdstillatelseEllerOppholdsPaSammeVilkar.setEffektueringsdato(
                xmlDateWsConverter.convert(oppholdSammeVilkaar.getOppholdSammeVilkaarEffektuering()));
        oppholdstillatelseEllerOppholdsPaSammeVilkar.setOppholdstillatelsePeriode(
                periodeWsConverter.convert(oppholdSammeVilkaar.getOppholdSammeVilkaarPeriode()));

        var oppholdstilatelse = new Oppholdstillatelse();
        oppholdstilatelse.setOppholdstillatelseType(oppholdSammeVilkaar.getOppholdstillatelseType());
        oppholdstilatelse.setVedtaksDato(
                xmlDateWsConverter.convert(oppholdSammeVilkaar.getOppholdstillatelseVedtaksDato()));

        oppholdstillatelseEllerOppholdsPaSammeVilkar.setOppholdstillatelse(oppholdstilatelse);
        return oppholdstillatelseEllerOppholdsPaSammeVilkar;
    }
}

