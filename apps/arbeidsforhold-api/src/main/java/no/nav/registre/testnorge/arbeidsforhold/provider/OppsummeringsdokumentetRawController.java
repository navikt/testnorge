package no.nav.registre.testnorge.arbeidsforhold.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.adapter.OppsummeringsdokumentetRawAdapter;
import no.nav.registre.testnorge.arbeidsforhold.domain.Oppsummeringsdokumentet;
import no.nav.registre.testnorge.arbeidsforhold.domain.OppsummeringsdokumentetRawList;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentetDTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/oppsummeringsdokumenter/raw")
@RequiredArgsConstructor
public class OppsummeringsdokumentetRawController {
    private final OppsummeringsdokumentetRawAdapter oppsummeringsdokumentetRawAdapter;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<List<String>> getAllOpplysningspliktig(
            @RequestHeader("miljo") String miljo,
            @RequestParam("page") Integer page
    ) {
        var list = oppsummeringsdokumentetRawAdapter.fetchAll(miljo, page);
        return ResponseEntity
                .ok()
                .header("Number-Of-Pages", list.getNumberOfPages().toString())
                .body(list.getDocuments());
    }
}