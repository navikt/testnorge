package no.nav.registre.testnorge.bridge.kafkacloudbridge.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.dto.bridge.v1.ContentDTO;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.EndringsdokumentV2Producer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.OpprettelsesdokumentV2Producer;

@RestController
@RequestMapping("/api/v1/organisajon")
@RequiredArgsConstructor
public class OrganisasjonController {

    private final OpprettelsesdokumentV2Producer opprettelsesdokumentProducer;
    private final EndringsdokumentV2Producer endringsdokumentProducer;

    @SneakyThrows
    @PostMapping("/opprettelsesdokument")
    public ResponseEntity<HttpStatus> sendOpprettelsesdokument(@RequestBody ContentDTO dto) {
        var content = ByteBuffer.wrap(dto.getContent());
        opprettelsesdokumentProducer.send(dto.getKey(), Opprettelsesdokument.fromByteBuffer(content));
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @PostMapping("/endringsdokument")
    public ResponseEntity<HttpStatus> sendEndringsdokument(@RequestBody ContentDTO dto) {
        var content = ByteBuffer.wrap(dto.getContent());
        endringsdokumentProducer.send(dto.getKey(), Endringsdokument.fromByteBuffer(content));
        return ResponseEntity.ok().build();
    }

}
