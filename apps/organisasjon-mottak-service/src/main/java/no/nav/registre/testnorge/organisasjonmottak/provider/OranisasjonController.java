package no.nav.registre.testnorge.organisasjonmottak.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OpprettelsesdokumentProducer;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.OrganisasjonDTO;

@RestController
@RequestMapping("/api/v1/organisasjoner")
@RequiredArgsConstructor
public class OranisasjonController {
    private final OpprettelsesdokumentProducer opprettelsesdokumentProducer;


    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestHeader String miljo, @RequestBody OrganisasjonDTO dto) {
        opprettelsesdokumentProducer.send(
                UUID.randomUUID().toString(),
                dto.toOrganisasjonOpprettelsesdokument(miljo)
        );
        return ResponseEntity.noContent().build();
    }
}
