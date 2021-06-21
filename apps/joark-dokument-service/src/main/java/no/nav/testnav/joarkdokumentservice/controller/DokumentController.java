package no.nav.testnav.joarkdokumentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.joarkdokumentservice.controller.dto.DokumentInfoDTO;
import no.nav.testnav.joarkdokumentservice.domain.DokuemntType;
import no.nav.testnav.joarkdokumentservice.domain.DokumentInfo;
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
        var dokumentInfoList = service.getDokumentInfoList(journalpostId, miljo);
        return ResponseEntity.ok(dokumentInfoList.stream().map(DokumentInfo::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{dokumentInfoId}")
    public ResponseEntity<String> hentDokument(
            @RequestHeader("miljo") String miljo,
            @PathVariable("dokumentInfoId") Integer dokumentInfoId,
            @PathVariable("journalpostId") Integer journalpostId
    ) {
        var dokument = service.getDokument(journalpostId, dokumentInfoId, DokuemntType.ORIGINAL, miljo);
        return ResponseEntity.ok(dokument);
    }

}
