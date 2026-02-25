package no.nav.testnav.joarkdokumentservice.controller.v2;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.joarkdokumentservice.consumer.dto.JournalpostDTO;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.joarkdokumentservice.service.DokumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/journalpost/{journalpostId}")
public class JournalpostController {

    private final DokumentService service;

    @GetMapping
    public Mono<JournalpostDTO> hentJournalpost(
            @RequestHeader("miljo") String miljo,
            @PathVariable("journalpostId") String journalpostId) {

        return service.getJournalpost(journalpostId, miljo);
    }

    @GetMapping("/dokumenter/{dokumentInfoId}")
    public Mono<ResponseEntity<String>> hentDokument(
            @RequestHeader("miljo") String miljo,
            @PathVariable("dokumentInfoId") String dokumentInfoId,
            @PathVariable("journalpostId") String journalpostId,
            @RequestParam DokumentType dokumentType
    ) {
        return service.getDokument(journalpostId, dokumentInfoId, dokumentType, miljo)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/dokumenter/{dokumentInfoId}/pdf")
    public Mono<ResponseEntity<byte[]>> hentPDF(
            @RequestHeader("miljo") String miljo,
            @PathVariable("dokumentInfoId") String dokumentInfoId,
            @PathVariable("journalpostId") String journalpostId
    ) {
        return service.getPDF(journalpostId, dokumentInfoId, miljo)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
