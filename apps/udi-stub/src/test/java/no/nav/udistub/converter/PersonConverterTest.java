package no.nav.udistub.converter;


import no.nav.udistub.converter.ws.ArbeidsadgangWsConverter;
import no.nav.udistub.converter.ws.BeskyttleseUnderBehandlingWsConverter;
import no.nav.udistub.converter.ws.GjeldendeOppholdStatusWsConverter;
import no.nav.udistub.converter.ws.GjeldendePersonWsConverter;
import no.nav.udistub.converter.ws.PersonWsConverter;
import no.nav.udistub.converter.ws.XmlDateWsConverter;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static no.nav.udistub.converter.DefaultTestData.TEST_PERSON_FNR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class PersonConverterTest extends ConverterTestBase {

    @Mock
    private GjeldendeOppholdStatusWsConverter gjeldendeOppholdStatusWsConverter;

    @Mock
    private XmlDateWsConverter xmlDateWsConverter;

    @Mock
    private GjeldendePersonWsConverter gjeldendePersonWsConverter;

    @Mock
    private ArbeidsadgangWsConverter arbeidsadgangWsConverter;

    @Mock
    private BeskyttleseUnderBehandlingWsConverter beskyttleseUnderBehandlingWsConverter;

	@InjectMocks
	protected PersonWsConverter personUDIConverter;

	@Test
	void convertFromPersonToArbeidsadgangIfPresent() {
		HentPersonstatusResultat result = personUDIConverter.convert(defaultTestPerson);
		assertNotNull(result);
		assertEquals(TEST_PERSON_FNR, result.getForesporselsfodselsnummer());
	}

	@Test
	void convertFromPersonToArbeidsadgangIfAbsent() {
		HentPersonstatusResultat result = personUDIConverter.convert(null);
		assertNull(result);
	}
}