package no.nav.registre.core.converter;

import static junit.framework.TestCase.assertNull;
import static no.nav.registre.core.DefaultTestData.TEST_ARBEIDOMGANGKATEGORI;
import static no.nav.registre.core.DefaultTestData.TEST_ARBEIDSADGANG;
import static no.nav.registre.core.DefaultTestData.TEST_ARBEIDSADGANG_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArbeidsgangPersonConverterTest extends ConverterTestBase {

	@InjectMocks
	protected ArbeidsadgangConverter arbeidsadgangConverter;

	@Test
	public void convertFromPersonToArbeidsadgangIfPresent() {
		no.udi.mt_1067_nav_data.v1.Arbeidsadgang result = arbeidsadgangConverter.convert(defaultTestPerson);
		assertNotNull(result);
		assertEquals(TEST_ARBEIDSADGANG, result.getHarArbeidsadgang());
		assertEquals(TEST_ARBEIDOMGANGKATEGORI, result.getArbeidsOmfang());
		assertEquals(TEST_ARBEIDSADGANG_TYPE, result.getTypeArbeidsadgang());
		assertNull(result.getArbeidsadgangsPeriode());
	}

	@Test
	public void convertFromPersonToArbeidsadgangIfAbsent() {
		no.udi.mt_1067_nav_data.v1.Arbeidsadgang result = arbeidsadgangConverter.convert(null);
		assertNull(result);
	}
}