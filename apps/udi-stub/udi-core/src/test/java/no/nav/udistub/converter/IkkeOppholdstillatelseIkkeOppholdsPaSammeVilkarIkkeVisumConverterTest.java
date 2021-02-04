package no.nav.udistub.converter;

import no.nav.udistub.converter.ws.IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumWsConverter;
import no.udi.mt_1067_nav_data.v1.AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum;
import no.udi.mt_1067_nav_data.v1.OppholdsgrunnlagKategori;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import no.nav.udistub.core.DefaultTestData;

@ExtendWith(MockitoExtension.class)
public class IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverterTest extends ConverterTestBase {

	@InjectMocks
	protected IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumWsConverter ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter;

	@Test
	public void convertFromPersonToIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumIfPresent() {
		IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum result = ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter.convert(
				defaultTestPerson.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum());

		assertNotNull(result);
		Assertions.assertEquals(DefaultTestData.TEST_ovrigIkkeOppholdsKategori, result.getOvrigIkkeOpphold().getArsak());

		Assertions.assertEquals(DefaultTestData.TEST_INNREISEFORBUD, result.getUtvistMedInnreiseForbud().getInnreiseForbud());
		Assertions.assertEquals(DefaultTestData.TEST_VARIGHET_UDI, result.getUtvistMedInnreiseForbud().getVarighet());

		AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak avslagEllerBortfall = result.getAvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak();
		assertEquals(EOSellerEFTAGrunnlagskategoriOppholdsrett.FAMILIE, avslagEllerBortfall.getAvslagPaSoknadOmOppholdsrettRealitetsBehandlet().getAvslagsGrunnlagEOS());
		assertEquals(EOSellerEFTAGrunnlagskategoriOppholdstillatelse.ARBEID, avslagEllerBortfall.getAvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet().getAvslagsGrunnlagEOS());
		assertEquals(OppholdsgrunnlagKategori.ARBEID, avslagEllerBortfall.getAvslagPaSoknadOmOppholdstillatelseRealitetsBehandlet().getAvslagsGrunnlagOvrig());
	}

	@Test
	public void convertFromPersonToIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumIfAbsent() {
		IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum result = ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumConverter.convert(null);
		assertNull(result);
	}
}