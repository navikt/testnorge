package no.nav.identpool.ident.rest.v1;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.exception.IllegalIdenttypeException;
import no.nav.identpool.ident.service.IdentpoolService;

@Slf4j
@RestController
@RequestMapping("/identifikator")
@RequiredArgsConstructor
public class IdentpoolController {
    private final IdentpoolService identpoolService;

    @GetMapping("/liste")
    public List<String> get(
            @RequestParam(value = "antall") Integer antall,
            @RequestParam(value = "identtype", defaultValue = "UBESTEMT") String identtypeString
    ) throws IllegalIdenttypeException {
        Pageable pageable = PageRequest.of(0, antall);
        Identtype identtype = Identtype.enumFromString(identtypeString);
        return identpoolService.findIdents(identtype, pageable);
    }

    @GetMapping("/ledig")
    public ResponseEntity<Boolean> erLedig(
            @RequestParam String personidentifkator
    ) {
        return ResponseEntity.ok(identpoolService.erLedig(personidentifkator));
    }

    @PostMapping("/bruk")
    public ResponseEntity<String> markerBrukt(
            @RequestParam String personidentifikator
    ) {
        return ResponseEntity.ok(identpoolService.markerBrukt(personidentifikator));
    }
}
