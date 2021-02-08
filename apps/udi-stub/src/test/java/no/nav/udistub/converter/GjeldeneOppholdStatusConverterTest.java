package no.nav.udistub.converter;

import no.nav.udistub.converter.ws.GjeldendeOppholdStatusWsConverter;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTABeslutningOmOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAVedtakOmVarigOppholdsrett;
import no.udi.mt_1067_nav_data.v1.GjeldendeOppholdsstatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class GjeldeneOppholdStatusConverterTest extends ConverterTestBase {

	private static final EOSellerEFTAVedtakOmVarigOppholdsrett TEST_eosEllerEFTAVedtakOmVarigOppholdsrett = createEOSellerEFTAVedtakOmVarigOppholdsrett();
	private static final EOSellerEFTABeslutningOmOppholdsrett TEST_eosEllerEFTABeslutningOmOppholdsrett = createEOSellerEFTABeslutningOmOppholdsrett();
	private static final EOSellerEFTAOppholdstillatelse TEST_eosEllerEFTAOppholdstillatelse = createEOSellerEFTAOppholdstillatelse();

	@InjectMocks
	protected GjeldendeOppholdStatusWsConverter gjeldeneOppholdStatusConverter;

	@Test
	void convertFromPersonToArbeidsadgangIfPresent() {
		GjeldendeOppholdsstatus result = gjeldeneOppholdStatusConverter.convert(defaultTestPerson.getOppholdStatus());
		assertNotNull(result);
		assertNotNull(result.getEOSellerEFTAOpphold());

		assertEquals(TEST_eosEllerEFTAVedtakOmVarigOppholdsrett.getEOSOppholdsgrunnlag(),
				result.getEOSellerEFTAOpphold().getEOSellerEFTAVedtakOmVarigOppholdsrett().getEOSOppholdsgrunnlag());
		assertEquals(TEST_eosEllerEFTABeslutningOmOppholdsrett.getEOSOppholdsgrunnlag(),
				result.getEOSellerEFTAOpphold().getEOSellerEFTABeslutningOmOppholdsrett().getEOSOppholdsgrunnlag());
		assertEquals(TEST_eosEllerEFTAOppholdstillatelse.getEOSOppholdsgrunnlag(),
				result.getEOSellerEFTAOpphold().getEOSellerEFTAOppholdstillatelse().getEOSOppholdsgrunnlag());
	}

	private static EOSellerEFTAOppholdstillatelse createEOSellerEFTAOppholdstillatelse() {
		EOSellerEFTAOppholdstillatelse eosEllerEFTAOppholdstillatelse = new EOSellerEFTAOppholdstillatelse();
		eosEllerEFTAOppholdstillatelse.setEOSOppholdsgrunnlag(EOSellerEFTAGrunnlagskategoriOppholdstillatelse.ARBEID);
		return eosEllerEFTAOppholdstillatelse;
	}

	private static EOSellerEFTAVedtakOmVarigOppholdsrett createEOSellerEFTAVedtakOmVarigOppholdsrett() {
		EOSellerEFTAVedtakOmVarigOppholdsrett eosEllerEFTAVedtakOmVarigOppholdsrett = new EOSellerEFTAVedtakOmVarigOppholdsrett();
		eosEllerEFTAVedtakOmVarigOppholdsrett.setEOSOppholdsgrunnlag(EOSellerEFTAGrunnlagskategoriOppholdsrett.FAMILIE);
		return eosEllerEFTAVedtakOmVarigOppholdsrett;
	}

	private static EOSellerEFTABeslutningOmOppholdsrett createEOSellerEFTABeslutningOmOppholdsrett() {
		EOSellerEFTABeslutningOmOppholdsrett eosEllerEFTABeslutningOmOppholdsrett = new EOSellerEFTABeslutningOmOppholdsrett();
		eosEllerEFTABeslutningOmOppholdsrett.setEOSOppholdsgrunnlag(EOSellerEFTAGrunnlagskategoriOppholdsrett.FAMILIE);
		return eosEllerEFTABeslutningOmOppholdsrett;
	}

	@Test
	void convertFromPersonToArbeidsadgangIfAbsent() {
		GjeldendeOppholdsstatus result = gjeldeneOppholdStatusConverter.convert(null);
		assertNull(result);
	}
}