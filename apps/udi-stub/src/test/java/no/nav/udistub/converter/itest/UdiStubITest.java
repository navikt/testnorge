package no.nav.udistub.converter.itest;

import ma.glasnost.orika.MapperFacade;
import no.nav.udistub.database.repository.PersonRepository;
import no.nav.udistub.provider.rs.PersonController;
import no.nav.udistub.service.dto.UdiPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

import static no.nav.udistub.converter.DefaultTestData.TEST_DATE;
import static no.nav.udistub.converter.DefaultTestData.TEST_FLYKTNINGSTATUS;
import static no.nav.udistub.converter.DefaultTestData.TEST_INNREISEFORBUD;
import static no.nav.udistub.converter.DefaultTestData.TEST_NAVN;
import static no.nav.udistub.converter.DefaultTestData.TEST_OPPHOLDSTILLATELSE;
import static no.nav.udistub.converter.DefaultTestData.TEST_OPPHOLDS_GRUNNLAG_KATEGORI;
import static no.nav.udistub.converter.DefaultTestData.TEST_PERSON_ALIAS_FNR;
import static no.nav.udistub.converter.DefaultTestData.TEST_PERSON_FNR;
import static no.nav.udistub.converter.DefaultTestData.TEST_ovrigIkkeOppholdsKategori;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import no.nav.udistub.database.model.Person;

class UdiStubITest extends ITestBase {

    private Person testPerson;
    private static final String PERSON_URI = "/api/v1/person";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    @Inject
    protected TestRestTemplate restTemplate;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MapperFacade mapperFacade;

    @BeforeEach
    void mapToTestPerson() {
        Person personEntity = mapperFacade.map(TESTPERSON_UDI, Person.class);
        personEntity.getAvgjoerelser().forEach(avgjorelseTo -> avgjorelseTo.setPerson(personEntity));
        personEntity.getAliaser().forEach(aliasTo -> aliasTo.setPerson(personEntity));
        personEntity.getOppholdStatus().setPerson(personEntity);
        personEntity.getArbeidsadgang().setPerson(personEntity);
        testPerson = personEntity;
    }

    @Test
    @Transactional
    void shouldOpprettPersonAndStoreInDb() throws Exception {
        String requestBody = getJsonContentsAsString("opprettPersonRequest-happy.json");
        ResponseEntity<PersonController.PersonControllerResponse> response = callOpprettPerson(requestBody);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Person> storedPersonOptional = personRepository.findByIdent(TEST_PERSON_FNR);

        assertTrue(storedPersonOptional.isPresent());
        Person storedPerson = storedPersonOptional.get();

        assertNotNull(storedPerson);
        assertEquals(TEST_DATE, storedPerson.getFoedselsDato());
        assertEquals(TEST_PERSON_FNR, storedPerson.getIdent());
        assertEquals(TEST_NAVN.getFornavn(), storedPerson.getNavn().getFornavn());
        assertEquals(TEST_NAVN.getMellomnavn(), storedPerson.getNavn().getMellomnavn());
        //assertEquals(TEST_NAVN.getEtternavn(), storedPerson.getNavn().getEtternavn());
        assertEquals(TEST_FLYKTNINGSTATUS, storedPerson.getFlyktning());
        assertEquals(TEST_OPPHOLDSTILLATELSE, storedPerson.getHarOppholdsTillatelse());

        assertNotNull(storedPerson.getAliaser());
        assertEquals(1, storedPerson.getAliaser().size());
        assertEquals(TEST_PERSON_ALIAS_FNR, storedPerson.getAliaser().get(0).getFnr());

        assertNotNull(storedPerson.getOppholdStatus());
        assertEquals(TEST_OPPHOLDS_GRUNNLAG_KATEGORI, storedPerson.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum().getAvslagEllerBortfall().getAvslagGrunnlagOverig());
        assertEquals(TEST_ovrigIkkeOppholdsKategori, storedPerson.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum().getOvrigIkkeOppholdsKategoriArsak());
        assertEquals(TEST_INNREISEFORBUD, storedPerson.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum().getUtvistMedInnreiseForbud().getInnreiseForbud());

        assertNotNull(storedPerson.getAvgjoerelser());
    }

    @Test
    @Disabled
    void shouldFindPersonByFnr() throws Exception {
        clearDatabase();
        storeTestPerson();
        ResponseEntity<PersonController.PersonControllerResponse> response = callFinnPerson();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getPerson());

        UdiPerson storedPerson = response.getBody().getPerson();
        assertEquals(TEST_FLYKTNINGSTATUS, storedPerson.getFlyktning());
        assertEquals(TEST_OPPHOLDSTILLATELSE, storedPerson.getHarOppholdsTillatelse());

        assertNotNull(storedPerson.getAliaser());
        assertNotNull(storedPerson.getOppholdStatus());

        assertEquals(TEST_OPPHOLDS_GRUNNLAG_KATEGORI, storedPerson.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum().getAvslagEllerBortfall().getAvslagGrunnlagOverig());
        assertEquals(TEST_ovrigIkkeOppholdsKategori, storedPerson.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum().getOvrigIkkeOppholdsKategoriArsak());
        assertEquals(TEST_INNREISEFORBUD, storedPerson.getOppholdStatus().getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum().getUtvistMedInnreiseForbud().getInnreiseForbud());
    }

    @Test
    @Disabled
    void shouldDeletePerson() throws Exception {
        clearDatabase();
        storeTestPerson();
        ResponseEntity<PersonController.PersonControllerResponse> response = callDeletePerson();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<Person> storedPersonOptional = personRepository.findByIdent(TEST_PERSON_FNR);
        assertTrue(storedPersonOptional.isEmpty());
    }

    private ResponseEntity<PersonController.PersonControllerResponse> callFinnPerson() {
        return restTemplate.exchange(PERSON_URI, HttpMethod.GET, createHttpEntity(), PersonController.PersonControllerResponse.class);
    }

    private ResponseEntity<PersonController.PersonControllerResponse> callOpprettPerson(String body) {
        return this.restTemplate.exchange(PERSON_URI, HttpMethod.POST, createHttpEntityWithBody(body), PersonController.PersonControllerResponse.class);
    }

    private ResponseEntity<PersonController.PersonControllerResponse> callDeletePerson() {
        return this.restTemplate.exchange(PERSON_URI, HttpMethod.DELETE, createHttpEntity(), PersonController.PersonControllerResponse.class);
    }

    private HttpEntity createHttpEntity() {
        return new HttpEntity(createHeaders());
    }

    private HttpEntity createHttpEntityWithBody(String body) {
        return new HttpEntity(body, createHeaders());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add(NAV_PERSON_IDENT, TEST_PERSON_FNR);
        headers.add(NAV_CONSUMER_ID, "test");
        return headers;
    }

    @Transactional
    public void clearDatabase() {
        personRepository.deleteAll();
    }

    @Transactional
    public void storeTestPerson() {
        personRepository.save(testPerson);
    }
}
