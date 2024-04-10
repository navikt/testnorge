package no.nav.testnav.apps.tpsmessagingservice.provider.v2;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.PersonService;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v2/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping("/ident")
    public List<PersonMiljoeDTO> getPerson(@RequestBody String ident,
                                           @RequestParam(required = false) List<String> miljoer) {

        return personService.getPerson(ident, nonNull(miljoer) ? miljoer : emptyList());
    }
}

