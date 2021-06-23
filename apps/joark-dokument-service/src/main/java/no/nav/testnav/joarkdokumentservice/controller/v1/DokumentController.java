package no.nav.testnav.joarkdokumentservice.controller.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.joarkdokumentservice.controller.v1.dto.DokumentInfoDTO;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.joarkdokumentservice.service.DokumentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/journalpost/{journalpostId}/dokumenter")
public class DokumentController {

    private final DokumentService service;

    @GetMapping
    public ResponseEntity<List<DokumentInfoDTO>> hentDokumenter(
            @RequestHeader("miljo") String miljo,
            @PathVariable("journalpostId") Integer journalpostId
    ) {
        var journalpost = service.getJournalpost(journalpostId, miljo);
        var list = journalpost.getDokumenter().stream().map(dokument -> DokumentInfoDTO
                .builder()
                .journalpostId(journalpost.getJournalpostId())
                .dokumentInfoId(dokument.getDokumentInfoId())
                .build()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{dokumentInfoId}")
    public ResponseEntity<String> hentDokument(
            @RequestHeader("miljo") String miljo,
            @PathVariable("dokumentInfoId") Integer dokumentInfoId,
            @PathVariable("journalpostId") Integer journalpostId
    ) {
        var dokument = service.getDokument(journalpostId, dokumentInfoId, DokumentType.ORIGINAL, miljo);
        return ResponseEntity.ok(dokument);
    }
}
