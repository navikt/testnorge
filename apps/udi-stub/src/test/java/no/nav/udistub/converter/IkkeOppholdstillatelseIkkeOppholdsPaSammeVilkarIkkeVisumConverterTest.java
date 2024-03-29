package no.nav.udistub.converter;

import no.nav.udistub.converter.ws.IkkeOppholdstillatelseIkkeOppholdPaSammeVilkarIkkeVisumWsConverter;
import no.udi.mt_1067_nav_data.v1.AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum;
import no.udi.mt_1067_nav_data.v1.OppholdsgrunnlagKategori;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverterTest extends ConverterTestBase {

    @InjectMocks
    protected IkkeOppholdstillatelseIkkeOppholdPaSammeVilkarIkkeVisumWsConverter ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter;

    @Test
    void convertFromPersonToIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumIfPresent() {
        IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum result = ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter.convert(
                defaultTestPerson.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum());

        assertNotNull(result);
        assertEquals(DefaultTestData.TEST_ovrigIkkeOppholdsKategori, result.getOvrigIkkeOpphold().getArsak());

        assertEquals(DefaultTestData.TEST_INNREISEFORBUD, result.getUtvistMedInnreiseForbud().getInnreiseForbud());
        assertEquals(DefaultTestData.TEST_VARIGHET_UDI, result.getUtvistMedInnreiseForbud().getVarighet());

        AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak avslagEllerBortfall = result.getAvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak();
        assertEquals(EOSellerEFTAGrunnlagskategoriOppholdsrett.FAMILIE, avslagEllerBortfall.getAvslagPaSoknadOmOppholdsrettRealitetsBehandlet().getAvslagsGrunnlagEOS());
        assertEquals(EOSellerEFTAGrunnlagskategoriOppholdstillatelse.ARBEID, avslagEllerBortfall.getAvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet().getAvslagsGrunnlagEOS());
        assertEquals(OppholdsgrunnlagKategori.ARBEID, avslagEllerBortfall.getAvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet().getAvslagsGrunnlagOvrig());
    }

    @Test
    void convertFromPersonToIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumIfAbsent() {
        IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum result = ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter.convert(null);
        assertNull(result);
    }
}