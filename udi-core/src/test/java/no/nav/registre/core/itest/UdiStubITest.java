package no.nav.registre.core.itest;

import static no.nav.registre.core.DefaultTestData.TEST_FLYKTNINGSTATUS;
import static no.nav.registre.core.DefaultTestData.TEST_ID;
import static no.nav.registre.core.DefaultTestData.TEST_NAVN;
import static no.nav.registre.core.DefaultTestData.TEST_OPPHOLDSTILLATELSE;
import static no.nav.registre.core.DefaultTestData.TEST_PERSON_FNR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.nav.registre.core.DefaultTestData;
import no.nav.registre.core.config.AppConfig;
import no.nav.registre.core.database.model.Person;
import no.nav.registre.core.database.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@EnableAutoConfiguration()
@SpringBootTest(classes = {AppConfig.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("itest")
public class UdiStubITest {

	private static final String PERSON_URI = "/api/v1/person";
	private static final String NAV_PERSON_IDENT = "Nav-Personident";
	private static final Person testperson = DefaultTestData.createPerson();

	@Autowired
	private PersonRepository personRepository;

	@Inject
	protected TestRestTemplate restTemplate;

	@BeforeEach
	public void setupBefore() {

	}

	@Test
	@Transactional
	public void shouldOpprettPersonAndStoreInDb() throws Exception {
		String requestBody = getJsonContentsAsString("opprettPersonRequest-happy.json");
		ResponseEntity<String> response = callOpprettPerson(requestBody);

		assertNotNull(response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		Optional<Person> storedPersonOptional = personRepository.findByFnr(TEST_PERSON_FNR);

		assertTrue(storedPersonOptional.isPresent());
		Person storedPerson = storedPersonOptional.get();

		assertEquals(TEST_PERSON_FNR, storedPerson.getFnr());

		assertEquals(TEST_FLYKTNINGSTATUS, storedPerson.getFlyktning());
		assertEquals(TEST_OPPHOLDSTILLATELSE, storedPerson.getOppholdsTillatelse());
		assertEquals(TEST_ID, storedPerson.getId());

		assertEquals(TEST_NAVN.getFornavn(), storedPerson.getNavn().getFornavn());
		assertEquals(TEST_NAVN.getMellomnavn(), storedPerson.getNavn().getMellomnavn());
		assertEquals(TEST_NAVN.getEtternavn(), storedPerson.getNavn().getEtternavn());
		assertNotNull(storedPerson.getAvgjoerelser());
		assertNotNull(storedPerson.getAliaser());
		assertNotNull(storedPerson.getFoedselsDato());
		assertNotNull(storedPerson.getSoeknadOmBeskyttelseUnderBehandling());
	}

	@Test
	@Transactional
	public void shouldFindPersonByFnr() throws Exception {
		personRepository.save(testperson);
		ResponseEntity<String> response = callFinnPerson();

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(getJsonContentsAsString("findPersonByFnrResult-happy.json"), response.getBody());
	}

	@Test
	@Transactional
	public void shouldDeletePerson() throws Exception {
		personRepository.save(testperson);
		ResponseEntity<String> response = callDeletePerson();

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

		Optional<Person> storedPersonOptional = personRepository.findByFnr(TEST_PERSON_FNR);
		assertTrue(storedPersonOptional.isEmpty());
	}

	private ResponseEntity<String> callFinnPerson() {
		return this.restTemplate.exchange(PERSON_URI, HttpMethod.GET, createHttpEntity(), String.class);
	}

	private ResponseEntity<String> callOpprettPerson(String body) {
		return this.restTemplate.exchange(PERSON_URI, HttpMethod.POST, createHttpEntityWithBody(body), String.class);
	}

	private ResponseEntity<String> callDeletePerson() {
		return this.restTemplate.exchange(PERSON_URI, HttpMethod.DELETE, createHttpEntity(), String.class);
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
		return headers;
	}

	private String getJsonContentsAsString(String filename) throws IOException {
		File requestBodyFile = new ClassPathResource(filename).getFile();
		return new String(Files.readAllBytes(requestBodyFile.toPath()));
	}
}
