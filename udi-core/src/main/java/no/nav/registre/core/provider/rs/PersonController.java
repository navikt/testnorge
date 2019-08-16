package no.nav.registre.core.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.core.database.model.Person;
import no.nav.registre.core.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonController {

	private final PersonService personService;

	@PostMapping
	public ResponseEntity<List<Person>> opprettPerson(@Valid @RequestBody List<Person> person) {
		var personer = person.stream().map(personService::opprettPerson).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.CREATED).body(personer);
	}

	@GetMapping
	public ResponseEntity<Person> finnPerson(@RequestHeader(name = "Nav-Personident") String fnr) {
		return ResponseEntity.ok(personService.finnPerson(fnr));
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePerson(@RequestHeader(name = "Nav-Personident") String fnr) {
		personService.deletePerson(fnr);
	}
}
