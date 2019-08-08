package no.nav.registre.core.converter.udi;

import no.nav.registre.core.converter.BaseConverter;
import no.nav.registre.core.database.model.opphold.OppholdStatus;
import no.nav.registre.core.database.model.Person;
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
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class GjeldeneOppholdStatusConverter extends BaseConverter<Person, GjeldendeOppholdsstatus> {

    private final ConversionService conversionService;

    public GjeldeneOppholdStatusConverter(ConversionService conversionService) {
        this.conversionService = this.registerConverter(conversionService);
    }

    @Override
    public GjeldendeOppholdsstatus convert(Person person) {
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

    private EOSellerEFTABeslutningOmOppholdsrett getEOSellerEFTABeslutningOmOpphold(OppholdStatus oppholdsStatus) {
        var eoSellerEFTABeslutningOmOppholdsrett = new EOSellerEFTABeslutningOmOppholdsrett();
        eoSellerEFTABeslutningOmOppholdsrett.setEffektueringsdato(
                conversionService.convert(oppholdsStatus.getEoSellerEFTABeslutningOmOppholdsrettEffektuering(), XMLGregorianCalendar.class));
        eoSellerEFTABeslutningOmOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEoSellerEFTABeslutningOmOppholdsrett());
        eoSellerEFTABeslutningOmOppholdsrett.setOppholdsrettsPeriode(
                conversionService.convert(oppholdsStatus.getEoSellerEFTABeslutningOmOppholdsrettPeriode(), Periode.class));
        return eoSellerEFTABeslutningOmOppholdsrett;
    }

    private EOSellerEFTAVedtakOmVarigOppholdsrett getEOSellerEFTAVedtakOmVarigOppholdrett(OppholdStatus oppholdsStatus) {
        var eoSellerEFTAVedtakOmVarigOppholdsrett = new EOSellerEFTAVedtakOmVarigOppholdsrett();
        eoSellerEFTAVedtakOmVarigOppholdsrett.setEffektueringsdato(
                conversionService.convert(oppholdsStatus.getEoSellerEFTAVedtakOmVarigOppholdsrettEffektuering(), XMLGregorianCalendar.class));
        eoSellerEFTAVedtakOmVarigOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEoSellerEFTAVedtakOmVarigOppholdsrett());
        eoSellerEFTAVedtakOmVarigOppholdsrett.setOppholdsrettsPeriode(
                conversionService.convert(oppholdsStatus.getEoSellerEFTAVedtakOmVarigOppholdsrettPeriode(), Periode.class));

        return eoSellerEFTAVedtakOmVarigOppholdsrett;
    }

    private EOSellerEFTAOppholdstillatelse getEoSellerEFTAOppholdstillatelse(OppholdStatus oppholdStatus) {
        var eoSellerEFTAOppholdstillatelse = new EOSellerEFTAOppholdstillatelse();
        eoSellerEFTAOppholdstillatelse.setEffektueringsdato(
                conversionService.convert(oppholdStatus.getEoSellerEFTAOppholdstillatelseEffektuering(), XMLGregorianCalendar.class));
        eoSellerEFTAOppholdstillatelse.setEOSOppholdsgrunnlag(oppholdStatus.getEoSellerEFTAOppholdstillatelse());
        eoSellerEFTAOppholdstillatelse.setOppholdstillatelsePeriode(
                conversionService.convert(oppholdStatus.getEoSellerEFTAVedtakOmVarigOppholdsrettPeriode(), Periode.class));

        return eoSellerEFTAOppholdstillatelse;
    }

    private OppholdstillatelseEllerOppholdsPaSammeVilkar getOppholdstillatelseEllerOppholdsPaSammeVilkar(OppholdStatus oppholdStatus) {
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

