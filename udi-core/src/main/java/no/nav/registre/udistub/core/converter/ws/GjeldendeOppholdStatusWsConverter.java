package no.nav.registre.udistub.core.converter.ws;

import no.nav.registre.udistub.core.service.to.UdiPerson;
import no.nav.registre.udistub.core.service.to.opphold.UdiOppholdStatus;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTABeslutningOmOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAOpphold;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAVedtakOmVarigOppholdsrett;
import no.udi.mt_1067_nav_data.v1.GjeldendeOppholdsstatus;
import no.udi.mt_1067_nav_data.v1.IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum;
import no.udi.mt_1067_nav_data.v1.Oppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.OppholdstillatelseEllerOppholdsPaSammeVilkar;
import no.udi.mt_1067_nav_data.v1.Periode;
import no.udi.mt_1067_nav_data.v1.Uavklart;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class GjeldendeOppholdStatusWsConverter implements Converter<UdiPerson, GjeldendeOppholdsstatus> {

    private final ConversionService conversionService;

    public GjeldendeOppholdStatusWsConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public GjeldendeOppholdsstatus convert(UdiPerson person) {
        if (person != null) {
            var resultatOppholdsstatus = new GjeldendeOppholdsstatus();
            var oppholdStatus = person.getOppholdStatus();

            resultatOppholdsstatus.setUavklart(Boolean.TRUE.equals(oppholdStatus.getUavklart()) ? new Uavklart() : null);

            var eoSellerEFTAOpphold = new EOSellerEFTAOpphold();
            eoSellerEFTAOpphold.setEOSellerEFTABeslutningOmOppholdsrett(getEOSellerEFTABeslutningOmOpphold(oppholdStatus));
            eoSellerEFTAOpphold.setEOSellerEFTAVedtakOmVarigOppholdsrett(getEOSellerEFTAVedtakOmVarigOppholdrett(oppholdStatus));
            eoSellerEFTAOpphold.setEOSellerEFTAOppholdstillatelse(getEoSellerEFTAOppholdstillatelse(oppholdStatus));

            resultatOppholdsstatus.setEOSellerEFTAOpphold(eoSellerEFTAOpphold);
            resultatOppholdsstatus.setOppholdstillatelseEllerOppholdsPaSammeVilkar(getOppholdstillatelseEllerOppholdsPaSammeVilkar(oppholdStatus));
            resultatOppholdsstatus.setIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(
                    conversionService.convert(person, IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.class));

            return resultatOppholdsstatus;
        }
        return null;
    }

    private EOSellerEFTABeslutningOmOppholdsrett getEOSellerEFTABeslutningOmOpphold(UdiOppholdStatus oppholdsStatus) {
        var eoSellerEFTABeslutningOmOppholdsrett = new EOSellerEFTABeslutningOmOppholdsrett();
        eoSellerEFTABeslutningOmOppholdsrett.setEffektueringsdato(
                conversionService.convert(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrettEffektuering(), XMLGregorianCalendar.class));
        eoSellerEFTABeslutningOmOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrett());
        eoSellerEFTABeslutningOmOppholdsrett.setOppholdsrettsPeriode(
                conversionService.convert(oppholdsStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode(), Periode.class));
        return eoSellerEFTABeslutningOmOppholdsrett;
    }

    private EOSellerEFTAVedtakOmVarigOppholdsrett getEOSellerEFTAVedtakOmVarigOppholdrett(UdiOppholdStatus oppholdsStatus) {
        var eoSellerEFTAVedtakOmVarigOppholdsrett = new EOSellerEFTAVedtakOmVarigOppholdsrett();
        eoSellerEFTAVedtakOmVarigOppholdsrett.setEffektueringsdato(
                conversionService.convert(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering(), XMLGregorianCalendar.class));
        eoSellerEFTAVedtakOmVarigOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrett());
        eoSellerEFTAVedtakOmVarigOppholdsrett.setOppholdsrettsPeriode(
                conversionService.convert(oppholdsStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(), Periode.class));

        return eoSellerEFTAVedtakOmVarigOppholdsrett;
    }

    private EOSellerEFTAOppholdstillatelse getEoSellerEFTAOppholdstillatelse(UdiOppholdStatus oppholdStatus) {
        var eoSellerEFTAOppholdstillatelse = new EOSellerEFTAOppholdstillatelse();
        eoSellerEFTAOppholdstillatelse.setEffektueringsdato(
                conversionService.convert(oppholdStatus.getEosEllerEFTAOppholdstillatelseEffektuering(), XMLGregorianCalendar.class));
        eoSellerEFTAOppholdstillatelse.setEOSOppholdsgrunnlag(oppholdStatus.getEosEllerEFTAOppholdstillatelse());
        eoSellerEFTAOppholdstillatelse.setOppholdstillatelsePeriode(
                conversionService.convert(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(), Periode.class));

        return eoSellerEFTAOppholdstillatelse;
    }

    private OppholdstillatelseEllerOppholdsPaSammeVilkar getOppholdstillatelseEllerOppholdsPaSammeVilkar(UdiOppholdStatus oppholdStatus) {
        var oppholdstillatelseEllerOppholdsPaSammeVilkar = new OppholdstillatelseEllerOppholdsPaSammeVilkar();
        var opphold = oppholdStatus.getOppholdSammeVilkaar();

        oppholdstillatelseEllerOppholdsPaSammeVilkar.setEffektueringsdato(
                conversionService.convert(opphold.getOppholdSammeVilkaarEffektuering(), XMLGregorianCalendar.class));
        oppholdstillatelseEllerOppholdsPaSammeVilkar.setOppholdstillatelsePeriode(
                conversionService.convert(opphold.getOppholdSammeVilkaarPeriode(), Periode.class));

        var oppholdstilatelse = new Oppholdstillatelse();
        oppholdstilatelse.setOppholdstillatelseType(opphold.getOppholdstillatelseType());
        oppholdstilatelse.setVedtaksDato(
                conversionService.convert(opphold.getOppholdstillatelseVedtaksDato(), XMLGregorianCalendar.class));

        oppholdstillatelseEllerOppholdsPaSammeVilkar.setOppholdstillatelse(oppholdstilatelse);
        return oppholdstillatelseEllerOppholdsPaSammeVilkar;
    }
}

