package no.nav.udistub.converter;

import no.nav.udistub.service.dto.UdiPerson;
import org.junit.jupiter.api.BeforeEach;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

public class ConverterTestBase {

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
}
