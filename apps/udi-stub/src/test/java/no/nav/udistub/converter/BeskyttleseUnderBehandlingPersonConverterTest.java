package no.nav.udistub.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import no.nav.udistub.converter.ws.BeskyttleseUnderBehandlingWsConverter;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeskyttleseUnderBehandlingPersonConverterTest extends ConverterTestBase {

	@InjectMocks
	protected BeskyttleseUnderBehandlingWsConverter beskyttleseUnderBehandlingConverter;

	@Test
	void convertFromPersonToSoknadOmBeskyttelseUnderBehandlingIfPresent() {
		SoknadOmBeskyttelseUnderBehandling result = beskyttleseUnderBehandlingConverter.convert(defaultTestPerson);
		assertNotNull(result);
		assertEquals(JaNeiUavklart.JA, result.getErUnderBehandling());
	}

	@Test
	void convertFromPersonToSoknadOmBeskyttelseUnderBehandlingIfAbsent() {
		SoknadOmBeskyttelseUnderBehandling result = beskyttleseUnderBehandlingConverter.convert(null);
		assertNull(result);
	}
}
