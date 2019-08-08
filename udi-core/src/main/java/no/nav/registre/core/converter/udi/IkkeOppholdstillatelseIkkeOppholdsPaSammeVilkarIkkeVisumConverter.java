package no.nav.registre.core.converter.udi;

import no.nav.registre.core.converter.BaseConverter;
import no.nav.registre.core.database.model.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.nav.registre.core.database.model.Person;
import no.udi.mt_1067_nav_data.v1.AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak;
import no.udi.mt_1067_nav_data.v1.AvslagPaSoknadOmOppholdsrettRealitetsBehandlet;
import no.udi.mt_1067_nav_data.v1.AvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet;
import no.udi.mt_1067_nav_data.v1.BortfallAvPOellerBOS;
import no.udi.mt_1067_nav_data.v1.FormeltVedtak;
import no.udi.mt_1067_nav_data.v1.IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum;
import no.udi.mt_1067_nav_data.v1.TilbakeKall;
import no.udi.mt_1067_nav_data.v1.UtvistMedInnreiseForbud;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter
        extends BaseConverter<Person, IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum> {

    private final ConversionService conversionService;

    IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter(ConversionService conversionService) {
        this.conversionService = this.registerConverter(conversionService);
    }

    @Override
    public IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum convert(Person person) {
        var ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum = new IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum();
        var ikkeOpphold = person.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum();

        ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                .setUtvistMedInnreiseForbud(getUtvistMedInnreiseForbud(ikkeOpphold));
        ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                .setAvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak(getAvslagEllerBortfall(ikkeOpphold));

        return ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum;
    }

    private UtvistMedInnreiseForbud getUtvistMedInnreiseForbud(IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOpphold) {
        var udiUtvistMedInnreiseForbud = new UtvistMedInnreiseForbud();
        var ikkeOppholdUvistMedInnreiseForbud = ikkeOpphold.getUtvistMedInnreiseForbud();
        udiUtvistMedInnreiseForbud.setVarighet(ikkeOppholdUvistMedInnreiseForbud.getVarighet());
        udiUtvistMedInnreiseForbud.setInnreiseForbud(ikkeOppholdUvistMedInnreiseForbud.getInnreiseForbud());
        udiUtvistMedInnreiseForbud.setVedtaksDato(
                conversionService.convert(ikkeOppholdUvistMedInnreiseForbud.getInnreiseForbudVedtaksDato(), XMLGregorianCalendar.class));
        return udiUtvistMedInnreiseForbud;

    }

    private AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak getAvslagEllerBortfall(
            IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOpphold) {

        var udiAvslagEllerBortfall = new AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak();
        var ikkeOppholdAvslagEllerBortfall = ikkeOpphold.getAvslagEllerBortFall();

        var tilbakeKall = new TilbakeKall();
        tilbakeKall.setVirkningsDato(
                conversionService.convert(ikkeOppholdAvslagEllerBortfall.getTilbakeKallVirkningsDato(), XMLGregorianCalendar.class));
        tilbakeKall.setUtreiseFrist(
                conversionService.convert(ikkeOppholdAvslagEllerBortfall.getTilbakeKallUtreiseFrist(), XMLGregorianCalendar.class));

        var formeltVedtak = new FormeltVedtak();
        formeltVedtak.setUtreiseFrist(
                conversionService.convert(ikkeOppholdAvslagEllerBortfall.getFormeltVedtakUtreiseFrist(), XMLGregorianCalendar.class));
        var bortfallAvOPellerBOS = new BortfallAvPOellerBOS();
        bortfallAvOPellerBOS.setVirkningsDato(
                conversionService.convert(ikkeOppholdAvslagEllerBortfall.getBortfallAvPOellerBOSDato(), XMLGregorianCalendar.class));

        var avslagPaSoknadOppholdstillatelse = new AvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet();
        avslagPaSoknadOppholdstillatelse.setUtreiseFrist(
                conversionService.convert(ikkeOppholdAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletUtreiseFrist(), XMLGregorianCalendar.class));
        avslagPaSoknadOppholdstillatelse.setAvslagsGrunnlagOvrig(ikkeOppholdAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagOvrig());
        avslagPaSoknadOppholdstillatelse.setAvslagsGrunnlagEOS(ikkeOppholdAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagEOS());

        var avslagPaSoknadOppholdsrett = new AvslagPaSoknadOmOppholdsrettRealitetsBehandlet();
        avslagPaSoknadOppholdsrett.setAvslagsGrunnlagEOS(ikkeOppholdAvslagEllerBortfall.getAvslagOppholdsrettBehandlet());

        udiAvslagEllerBortfall.setTilbakeKall(tilbakeKall);
        udiAvslagEllerBortfall.setAvgjorelsesDato(
                conversionService.convert(ikkeOppholdAvslagEllerBortfall.getAvgjorelsesDato(), XMLGregorianCalendar.class));
        udiAvslagEllerBortfall.setFormeltVedtak(formeltVedtak);
        udiAvslagEllerBortfall.setBortfallAvPOellerBOS(bortfallAvOPellerBOS);
        udiAvslagEllerBortfall.setAvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet(avslagPaSoknadOppholdstillatelse);
        udiAvslagEllerBortfall.setAvslagPaSoknadOmOppholdsrettRealitetsBehandlet(avslagPaSoknadOppholdsrett);

        return udiAvslagEllerBortfall;
    }
}
