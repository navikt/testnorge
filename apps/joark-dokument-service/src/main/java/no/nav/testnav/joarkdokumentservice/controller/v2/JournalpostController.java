package no.nav.testnav.joarkdokumentservice.controller.v2;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.joarkdokumentservice.controller.v2.dto.JournalpostDTO;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.joarkdokumentservice.service.DokumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/journalpost/{journalpostId}")
public class JournalpostController {
    private final DokumentService service;

    @GetMapping
    public ResponseEntity<JournalpostDTO> hentJournalpost(
            @RequestHeader("miljo") String miljo,
            @PathVariable("journalpostId") Integer journalpostId
    ) {
        var journalpost = service.getJournalpost(journalpostId, miljo);
        return ResponseEntity.ok(journalpost.toDTO());
    }

    @GetMapping("/dokumenter/{dokumentInfoId}")
    public ResponseEntity<String> hentDokument(
            @RequestHeader("miljo") String miljo,
            @PathVariable("dokumentInfoId") Integer dokumentInfoId,
            @PathVariable("journalpostId") Integer journalpostId,
            @RequestParam DokumentType dokumentType
    ) {
        var dokument = service.getDokument(journalpostId, dokumentInfoId, dokumentType, miljo);

        if (dokument == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dokument);
    }

    @GetMapping("/dokumenter/{dokumentInfoId}/pdf")
    public ResponseEntity<byte[]> hentPDF(
            @RequestHeader("miljo") String miljo,
            @PathVariable("dokumentInfoId") Integer dokumentInfoId,
            @PathVariable("journalpostId") Integer journalpostId
    ) {
        var dokument = service.getPDF(journalpostId, dokumentInfoId, miljo);

        if (dokument == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dokument);
    }
}
