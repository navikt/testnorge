package no.nav.registre.testnorge.arena.provider.rs;

import java.util.Collections;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.arena.consumer.rs.PdlPersonConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.response.pdl.PdlPerson;
import no.nav.registre.testnorge.arena.service.PdlPersonService;

@RestController
@RequestMapping("api/v1/pdl")
@RequiredArgsConstructor
public class PdlPersonController {

    private final PdlPersonConsumer pdlPersonConsumer;
    private final PdlPersonService pdlPersonService;

    @GetMapping("/{ident}")
    public PdlPerson pdlPerson(@PathVariable("ident") String ident) {
        return pdlPersonConsumer.getPdlPerson(ident);
    }

    @GetMapping("/aktoer/{ident}")
    public Map<String, String> aktoerId(@PathVariable("ident") String ident) {
        return pdlPersonService.getAktoerIderTilIdenter(Collections.singletonList(ident));
    }

}
