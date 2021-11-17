package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.PersonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    public void getPerson(String ident, List<String> miljoer) {

        personService.getPerson(ident, miljoer);
    }
}
