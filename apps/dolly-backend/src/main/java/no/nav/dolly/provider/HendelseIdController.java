package no.nav.dolly.provider;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.service.HendelseIdService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RequestMapping("/api/v1/hendelseid")
@RestController
@RequiredArgsConstructor
public class HendelseIdController {

    private final HendelseIdService hendelseIdService;

    @GetMapping("/ident/{ident}")
    public Mono<JsonNode> getHendelserForIdent(@PathVariable String ident) {

        return hendelseIdService.getOrdreStatus(ident);
    }

    @GetMapping("/ident/{ident}/opplysningstype/{opplysningstype}")
    public Mono<List<JsonNode>> getHendelserForOpplysningstype(@PathVariable String ident, @PathVariable PdlArtifact opplysningstype) {

        return hendelseIdService.getOrdrerByArtifact(ident, opplysningstype);
    }

    @GetMapping("/ident/{ident}/opplysningstype/{opplysningstype}/id/{id}")
    public Mono<JsonNode> getHendelserForOpplysningstypeOgId(@PathVariable String ident,
                                                             @PathVariable PdlArtifact opplysningstype,
                                                             @PathVariable Integer id) {

        return hendelseIdService.getHendelseById(ident, opplysningstype, id);
    }
}
