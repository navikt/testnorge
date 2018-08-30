package no.nav.identpool.ident.rs.v1;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<String>> get(
            @RequestParam(value = "antall") Integer antall,
            @RequestParam(value = "identtype", required = false) String identtypeString
    ) throws IllegalIdenttypeException {
        Identtype identtype = Identtype.enumFromString(identtypeString);
        return ResponseEntity.ok(identpoolService.findIdents(antall, identtype));
    }

    @GetMapping("/ledig")
    public ResponseEntity<Boolean> erLedig(
            @RequestParam String personidentifkator
    ) {
        return ResponseEntity.ok(identpoolService.erLedig(personidentifkator));
    }
}
