package no.nav.registre.core.converter;

import static junit.framework.TestCase.assertNull;
import static no.nav.registre.core.DefaultTestData.TEST_PERSON_FNR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PersonUDIConverterTest extends ConverterTestBase {

	@InjectMocks
	protected PersonUDIConverter personUDIConverter;

	@Test
	public void convertFromPersonToArbeidsadgangIfPresent() {
		HentPersonstatusResultat result = personUDIConverter.convert(defaultTestPerson);
		assertNotNull(result);
		assertEquals(TEST_PERSON_FNR, result.getForesporselsfodselsnummer());

		assertNull(result.getArbeidsadgang());
		assertNull(result.getAvgjorelsehistorikk());
		assertNull(result.getGjeldendeOppholdsstatus());
		assertNull(result.getGjeldendePerson());
		assertNull(result.getSoknadOmBeskyttelseUnderBehandling());
		assertNull(result.getUttrekkstidspunkt());
	}

	@Test
	public void convertFromPersonToArbeidsadgangIfAbsent() {
		HentPersonstatusResultat result = personUDIConverter.convert(null);
		assertNull(result);
	}
}