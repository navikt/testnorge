package no.nav.udistub.converter.ws;

import no.nav.udistub.service.dto.opphold.UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.udi.mt_1067_nav_data.v1.AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak;
import no.udi.mt_1067_nav_data.v1.AvslagPaSoknadOmOppholdsrettRealitetsBehandlet;
import no.udi.mt_1067_nav_data.v1.AvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet;
import no.udi.mt_1067_nav_data.v1.BortfallAvPOellerBOS;
import no.udi.mt_1067_nav_data.v1.FormeltVedtak;
import no.udi.mt_1067_nav_data.v1.IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum;
import no.udi.mt_1067_nav_data.v1.OvrigIkkeOpphold;
import no.udi.mt_1067_nav_data.v1.TilbakeKall;
import no.udi.mt_1067_nav_data.v1.UtvistMedInnreiseForbud;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class IkkeOppholdstillatelseIkkeOppholdPaSammeVilkarIkkeVisumWsConverter
        implements Converter<UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum, IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum> {

    private final XmlDateWsConverter xmlDateWsConverter = new XmlDateWsConverter();

    @Override
    public IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum convert(UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOpphold) {

        if (nonNull(ikkeOpphold)) {
            var ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum = new IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum();

            ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                    .setUtvistMedInnreiseForbud(getUtvistMedInnreiseForbud(ikkeOpphold));
            ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                    .setAvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak(getAvslagEllerBortfall(ikkeOpphold));
            ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                    .setOvrigIkkeOpphold(getOvrigIkkeOpphold(ikkeOpphold));

            return ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum;
        }
        return null;
    }

    private UtvistMedInnreiseForbud getUtvistMedInnreiseForbud(UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOpphold) {

        UtvistMedInnreiseForbud udiUtvistMedInnreiseForbud = null;
        var ikkeOppholdUvistMedInnreiseForbud = ikkeOpphold.getUtvistMedInnreiseForbud();

        if (nonNull(ikkeOppholdUvistMedInnreiseForbud)) {
            udiUtvistMedInnreiseForbud = new UtvistMedInnreiseForbud();
            udiUtvistMedInnreiseForbud.setVarighet(ikkeOppholdUvistMedInnreiseForbud.getVarighet());
            udiUtvistMedInnreiseForbud.setInnreiseForbud(ikkeOppholdUvistMedInnreiseForbud.getInnreiseForbud());
            udiUtvistMedInnreiseForbud.setVedtaksDato(
                    xmlDateWsConverter.convert(ikkeOppholdUvistMedInnreiseForbud.getInnreiseForbudVedtaksDato()));
        }
        return udiUtvistMedInnreiseForbud;
    }

    private AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak getAvslagEllerBortfall(
            UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOpphold) {

        var udiAvslagEllerBortfall = new AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak();
        var ikkeOppholdAvslagEllerBortfall = ikkeOpphold.getAvslagEllerBortfall();

        if (nonNull(ikkeOppholdAvslagEllerBortfall)) {

            var tilbakeKall = new TilbakeKall();
            tilbakeKall.setVirkningsDato(
                    xmlDateWsConverter.convert(ikkeOppholdAvslagEllerBortfall.getTilbakeKallVirkningsDato()));
            tilbakeKall.setUtreiseFrist(
                    xmlDateWsConverter.convert(ikkeOppholdAvslagEllerBortfall.getTilbakeKallUtreiseFrist()));
            udiAvslagEllerBortfall.setTilbakeKall(tilbakeKall);

            var formeltVedtak = new FormeltVedtak();
            formeltVedtak.setUtreiseFrist(
                    xmlDateWsConverter.convert(ikkeOppholdAvslagEllerBortfall.getFormeltVedtakUtreiseFrist()));
            udiAvslagEllerBortfall.setFormeltVedtak(formeltVedtak);

            var bortfallAvOPellerBOS = new BortfallAvPOellerBOS();
            bortfallAvOPellerBOS.setVirkningsDato(
                    xmlDateWsConverter.convert(ikkeOppholdAvslagEllerBortfall.getBortfallAvPOellerBOSDato()));
            udiAvslagEllerBortfall.setBortfallAvPOellerBOS(bortfallAvOPellerBOS);

            var avslagPaSoknadOppholdstillatelse = new AvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet();
            avslagPaSoknadOppholdstillatelse.setUtreiseFrist(
                    xmlDateWsConverter.convert(ikkeOppholdAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletUtreiseFrist()));
            avslagPaSoknadOppholdstillatelse.setAvslagsGrunnlagOvrig(ikkeOppholdAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagOvrig());
            avslagPaSoknadOppholdstillatelse.setAvslagsGrunnlagEOS(ikkeOppholdAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagEOS());
            udiAvslagEllerBortfall.setAvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet(avslagPaSoknadOppholdstillatelse);


            var avslagPaSoknadOppholdsrett = new AvslagPaSoknadOmOppholdsrettRealitetsBehandlet();
            avslagPaSoknadOppholdsrett.setAvslagsGrunnlagEOS(ikkeOppholdAvslagEllerBortfall.getAvslagOppholdsrettBehandlet());
            udiAvslagEllerBortfall.setAvslagPaSoknadOmOppholdsrettRealitetsBehandlet(avslagPaSoknadOppholdsrett);

            udiAvslagEllerBortfall.setAvgjorelsesDato(
                    xmlDateWsConverter.convert(ikkeOppholdAvslagEllerBortfall.getAvgjorelsesDato()));
        }

        return udiAvslagEllerBortfall;
    }

    private OvrigIkkeOpphold getOvrigIkkeOpphold(UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOpphold) {

        OvrigIkkeOpphold ovrigIkkeOpphold = new OvrigIkkeOpphold();
        ovrigIkkeOpphold.setArsak(ikkeOpphold.getOvrigIkkeOppholdsKategoriArsak());
        return ovrigIkkeOpphold;
    }
}
