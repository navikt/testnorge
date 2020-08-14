package no.nav.registre.udistub.core.converter;

import no.nav.registre.udistub.core.TestSetupException;
import no.nav.registre.udistub.core.converter.ws.PeriodeWsConverter;
import no.nav.registre.udistub.core.converter.ws.XmlDateWsConverter;
import no.nav.registre.udistub.core.service.to.UdiPerson;
import no.nav.registre.udistub.core.DefaultTestData;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.convert.ConversionService;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;

public class ConverterTestBase {

	@InjectMocks
	protected PeriodeWsConverter periodeConverter;

	@InjectMocks
	protected XmlDateWsConverter xmlDateConverter;

	@Mock
	private ConversionService conversionService;

	protected UdiPerson defaultTestPerson;
	protected DatatypeFactory datatypeFactory;

	public ConverterTestBase() {
		try {
			this.datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new TestSetupException("Kunne ikke starte lage datatypefactory for gregorian calendar mapping");
		}
	}

	@BeforeEach
	void setUpTestPerson() {
		defaultTestPerson = DefaultTestData.createPersonTo();
	}

	protected XMLGregorianCalendar mapToXmlGregorianCalendar(Date date) {
		return datatypeFactory.newXMLGregorianCalendar(date.toString());
	}
}
