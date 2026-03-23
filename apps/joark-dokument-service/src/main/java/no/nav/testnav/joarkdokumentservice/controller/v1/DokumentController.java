package no.nav.testnav.joarkdokumentservice.controller.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.joarkdokumentservice.controller.v1.dto.DokumentInfoDTO;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.joarkdokumentservice.service.DokumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/journalpost/{journalpostId}/dokumenter")
public class DokumentController {

    private final DokumentService service;

    @GetMapping
    public Mono<ResponseEntity<List<DokumentInfoDTO>>> hentDokumenter(
            @RequestHeader("miljo") String miljo,
            @PathVariable("journalpostId") String journalpostId
    ) {
        return service.getJournalpost(journalpostId, miljo)
                .map(journalpost -> journalpost.getDokumenter().stream().map(dokument -> DokumentInfoDTO
                        .builder()
                        .journalpostId(journalpost.getJournalpostId())
                        .dokumentInfoId(dokument.getDokumentInfoId())
                        .build()
                ).toList())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{dokumentInfoId}")
    public Mono<ResponseEntity<String>> hentDokument(
            @RequestHeader("miljo") String miljo,
            @PathVariable("dokumentInfoId") String dokumentInfoId,
            @PathVariable("journalpostId") String journalpostId
    ) {
        return service.getDokument(journalpostId, dokumentInfoId, DokumentType.ORIGINAL, miljo)
                .map(ResponseEntity::ok);
    }
}
