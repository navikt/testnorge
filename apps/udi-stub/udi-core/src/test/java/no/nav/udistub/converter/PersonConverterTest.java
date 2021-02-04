package no.nav.udistub.converter;


import static no.nav.udistub.core.DefaultTestData.TEST_PERSON_FNR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import no.nav.udistub.converter.ws.PersonWsConverter;
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
	}

	@Test
	public void convertFromPersonToArbeidsadgangIfAbsent() {
		HentPersonstatusResultat result = personUDIConverter.convert(null);
		assertNull(result);
	}
}