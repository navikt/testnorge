package no.nav.registre.ereg.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.ereg.provider.rs.request.EregDataRequest;
import no.nav.registre.ereg.service.FlatfileService;

@Slf4j
@RestController
@RequestMapping("/api/v1/orkestrering")
@RequiredArgsConstructor
public class OrkestreringController {

    private final FlatfileService flatfileService;

    @PostMapping("/opprett")
    public ResponseEntity<String> opprettEnheterIEreg(@RequestBody List<EregDataRequest> data, @RequestParam boolean opplast) {
        String eregData = flatfileService.mapEreg(data, opplast);

        if ("".equals(eregData)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(eregData);
    }

}
