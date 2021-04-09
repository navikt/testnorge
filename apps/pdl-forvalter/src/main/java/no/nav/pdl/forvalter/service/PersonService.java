package no.nav.pdl.forvalter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.dto.BestillingRequest;
import no.nav.pdl.forvalter.dto.PdlPerson;
import no.nav.pdl.forvalter.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final ObjectMapper objectMapper;

    public JsonNode updatePerson(String ident, BestillingRequest request){

        var person = personRepository.findByIdent(ident);

        if (person.isPresent()) {
            try {
                var pdlPerson = objectMapper.readValue(person.get().getBody(), PdlPerson.class);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }


        return null;
    }
}
