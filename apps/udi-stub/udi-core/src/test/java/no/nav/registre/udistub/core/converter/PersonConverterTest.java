package no.nav.registre.udistub.core.converter;


import static no.nav.registre.udistub.core.DefaultTestData.TEST_PERSON_FNR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import no.nav.registre.udistub.core.converter.ws.PersonWsConverter;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PersonConverterTest extends ConverterTestBase {

	@InjectMocks
	protected PersonWsConverter personUDIConverter;

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