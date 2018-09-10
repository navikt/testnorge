package no.nav.identpool.ident.rest.v1;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;
import no.nav.identpool.ident.exception.IdentAlleredeIBrukException;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.service.IdentpoolService;

@Slf4j
@RestController
@RequestMapping("/identifikator/v1")
@RequiredArgsConstructor
public class IdentpoolController {
    private final IdentpoolService identpoolService;

    @GetMapping("/hent")
    public List<String> get(
            @RequestParam(value = "antall") Integer antall,
            @RequestParam(value = "identtype", defaultValue = "UBESTEMT") String identtypeString,
            @RequestParam(value = "foedtfoer", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate foedtFoer,
            @RequestParam(value = "foedtetter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate foedtEtter,
            @RequestParam(value = "kjoenn", defaultValue = "UBESTEMT") String kjoenn
    ) {
        HentIdenterRequest hentIdenterRequest = HentIdenterRequest.builder()
                .pageable(PageRequest.of(0, antall))
                .identtype(Identtype.enumFromString(identtypeString))
                .foedtFoer(foedtFoer)
                .foedtEtter(foedtEtter)
                .kjoenn(Kjoenn.enumFromString(kjoenn))
                .build();
        return identpoolService.findIdents(hentIdenterRequest);
    }

    @GetMapping("/ledig")
    public Boolean erLedig(
            @RequestParam String personidentifikator
    ) {
        return identpoolService.erLedig(personidentifikator);
    }

    @PostMapping("/bruk")
    public String markerBrukt(
            @RequestParam String personidentifikator
    ) throws IdentAlleredeIBrukException {
        return identpoolService.markerBrukt(personidentifikator);
    }

    @GetMapping("/les")
    public IdentEntity lesInnhold(
            @RequestParam(value = "personidentifikator") String personidentifikator
    ) {
        return identpoolService.lesInnhold(personidentifikator);
    }
}
