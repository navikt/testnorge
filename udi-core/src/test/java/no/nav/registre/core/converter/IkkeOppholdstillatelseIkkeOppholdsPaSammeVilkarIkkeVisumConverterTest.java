package no.nav.registre.core.converter;

import static junit.framework.TestCase.assertNull;
import static no.nav.registre.core.DefaultTestData.TEST_INNREISEFORBUD;
import static no.nav.registre.core.DefaultTestData.TEST_VARIGHET_UDI;
import static no.nav.registre.core.DefaultTestData.TEST_ovrigIkkeOppholdsKategori;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.udi.mt_1067_nav_data.v1.AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum;
import no.udi.mt_1067_nav_data.v1.OppholdsgrunnlagKategori;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverterTest extends ConverterTestBase {

	@InjectMocks
	protected IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter;

	@Test
	public void convertFromPersonToIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumIfPresent() {
		IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum result = ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter.convert(defaultTestPerson);

		assertNotNull(result);
		assertEquals(TEST_ovrigIkkeOppholdsKategori, result.getOvrigIkkeOpphold().getArsak());

		assertEquals(TEST_INNREISEFORBUD, result.getUtvistMedInnreiseForbud().getInnreiseForbud());
		assertEquals(TEST_VARIGHET_UDI, result.getUtvistMedInnreiseForbud().getVarighet());

		AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak avslagEllerBortfall = result.getAvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak();
		assertEquals(EOSellerEFTAGrunnlagskategoriOppholdsrett.FAMILIE, avslagEllerBortfall.getAvslagPaSoknadOmOppholdsrettRealitetsBehandlet().getAvslagsGrunnlagEOS());
		assertEquals(EOSellerEFTAGrunnlagskategoriOppholdstillatelse.ARBEID, avslagEllerBortfall.getAvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet().getAvslagsGrunnlagEOS());
		assertEquals(OppholdsgrunnlagKategori.ARBEID, avslagEllerBortfall.getAvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet().getAvslagsGrunnlagOvrig());
		assertNull(avslagEllerBortfall.getTilbakeKall().getUtreiseFrist());
		assertNull(avslagEllerBortfall.getTilbakeKall().getVirkningsDato());
		assertNull(avslagEllerBortfall.getFormeltVedtak().getUtreiseFrist());
		assertNull(avslagEllerBortfall.getAvgjorelsesDato());
		assertNull(avslagEllerBortfall.getBortfallAvPOellerBOS().getVirkningsDato());
	}

	@Test
	public void convertFromPersonToIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumIfAbsent() {
		IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum result = ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter.convert(null);
		assertNull(result);
	}
}