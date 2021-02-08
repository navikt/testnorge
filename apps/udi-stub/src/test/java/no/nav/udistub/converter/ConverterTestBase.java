package no.nav.udistub.converter;

import no.nav.udistub.converter.ws.PeriodeWsConverter;
import no.nav.udistub.converter.ws.XmlDateWsConverter;
import no.nav.udistub.service.dto.UdiPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;

public class ConverterTestBase {

	protected PeriodeWsConverter periodeConverter;

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
