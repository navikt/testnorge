package no.nav.registre.core.converter;

import static no.nav.registre.core.DefaultTestData.createPerson;

import no.nav.registre.core.TestSetupException;
import no.nav.registre.core.database.model.Person;
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
	protected PeriodeConverter periodeConverter;

	@InjectMocks
	protected XmlDateConverter xmlDateConverter;

	@Mock
	private ConversionService conversionService;

	protected Person defaultTestPerson;
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
		defaultTestPerson = createPerson();
	}

	protected XMLGregorianCalendar mapToXmlGregorianCalendar(Date date) {
		return datatypeFactory.newXMLGregorianCalendar(date.toString());
	}
}
