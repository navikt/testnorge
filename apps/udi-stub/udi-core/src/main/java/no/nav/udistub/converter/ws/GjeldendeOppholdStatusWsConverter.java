package no.nav.udistub.converter.ws;

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

import no.nav.udistub.service.dto.opphold.UdiOppholdSammeVilkaar;
import no.nav.udistub.service.dto.opphold.UdiOppholdStatus;

@Component
public class GjeldendeOppholdStatusWsConverter implements Converter<UdiOppholdStatus, GjeldendeOppholdsstatus> {

    private XmlDateWsConverter xmlDateWsConverter = new XmlDateWsConverter();
    private PeriodeWsConverter periodeWsConverter = new PeriodeWsConverter();

    public GjeldendeOppholdStatusWsConverter() {
    }

    @Override
    public GjeldendeOppholdsstatus convert(UdiOppholdStatus oppholdStatus) {
        if (oppholdStatus != null) {
            IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumWsConverter ikkeWsConverter;
            ikkeWsConverter = new IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumWsConverter();
            var resultatOppholdsstatus = new GjeldendeOppholdsstatus();

            resultatOppholdsstatus.setUavklart(Boolean.TRUE.equals(oppholdStatus.getUavklart()) ? new Uavklart() : null);

            var eoSellerEFTAOpphold = new EOSellerEFTAOpphold();
            eoSellerEFTAOpphold.setEOSellerEFTABeslutningOmOppholdsrett(getEOSellerEFTABeslutningOmOpphold(oppholdStatus));
            eoSellerEFTAOpphold.setEOSellerEFTAVedtakOmVarigOppholdsrett(getEOSellerEFTAVedtakOmVarigOppholdrett(oppholdStatus));
            eoSellerEFTAOpphold.setEOSellerEFTAOppholdstillatelse(getEoSellerEFTAOppholdstillatelse(oppholdStatus));

            resultatOppholdsstatus.setEOSellerEFTAOpphold(eoSellerEFTAOpphold);
            resultatOppholdsstatus.setOppholdstillatelseEllerOppholdsPaSammeVilkar(getOppholdstillatelseEllerOppholdsPaSammeVilkar(oppholdStatus.getOppholdSammeVilkaar()));
            resultatOppholdsstatus.setIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(
                    ikkeWsConverter.convert(oppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum()));

            return resultatOppholdsstatus;
        }
        return null;
    }

    private EOSellerEFTABeslutningOmOppholdsrett getEOSellerEFTABeslutningOmOpphold(UdiOppholdStatus oppholdsStatus) {
        var eoSellerEFTABeslutningOmOppholdsrett = new EOSellerEFTABeslutningOmOppholdsrett();
        eoSellerEFTABeslutningOmOppholdsrett.setEffektueringsdato(
                xmlDateWsConverter.convert(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrettEffektuering()));
        eoSellerEFTABeslutningOmOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrett());
        eoSellerEFTABeslutningOmOppholdsrett.setOppholdsrettsPeriode(
                periodeWsConverter.convert(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode()));
        return eoSellerEFTABeslutningOmOppholdsrett;
    }

    private EOSellerEFTAVedtakOmVarigOppholdsrett getEOSellerEFTAVedtakOmVarigOppholdrett(UdiOppholdStatus oppholdsStatus) {
        var eoSellerEFTAVedtakOmVarigOppholdsrett = new EOSellerEFTAVedtakOmVarigOppholdsrett();
        eoSellerEFTAVedtakOmVarigOppholdsrett.setEffektueringsdato(
                xmlDateWsConverter.convert(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering()));
        eoSellerEFTAVedtakOmVarigOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrett());
        eoSellerEFTAVedtakOmVarigOppholdsrett.setOppholdsrettsPeriode(
                periodeWsConverter.convert(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode()));

        return eoSellerEFTAVedtakOmVarigOppholdsrett;
    }

    private EOSellerEFTAOppholdstillatelse getEoSellerEFTAOppholdstillatelse(UdiOppholdStatus oppholdStatus) {
        var eoSellerEFTAOppholdstillatelse = new EOSellerEFTAOppholdstillatelse();
        eoSellerEFTAOppholdstillatelse.setEffektueringsdato(
                xmlDateWsConverter.convert(oppholdStatus.getEosEllerEFTAOppholdstillatelseEffektuering()));
        eoSellerEFTAOppholdstillatelse.setEOSOppholdsgrunnlag(oppholdStatus.getEosEllerEFTAOppholdstillatelse());
        eoSellerEFTAOppholdstillatelse.setOppholdstillatelsePeriode(
                periodeWsConverter.convert(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode()));

        return eoSellerEFTAOppholdstillatelse;
    }

    private OppholdstillatelseEllerOppholdsPaSammeVilkar getOppholdstillatelseEllerOppholdsPaSammeVilkar(UdiOppholdSammeVilkaar oppholdSammeVilkaar) {
        var oppholdstillatelseEllerOppholdsPaSammeVilkar = new OppholdstillatelseEllerOppholdsPaSammeVilkar();
        if (oppholdSammeVilkaar == null) {
            return null;
        }
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

