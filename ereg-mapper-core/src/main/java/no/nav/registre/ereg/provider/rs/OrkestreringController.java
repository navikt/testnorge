package no.nav.registre.ereg.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import no.nav.registre.ereg.provider.rs.request.EregDataRequest;
import no.nav.registre.ereg.service.FlatfileService;

@Slf4j
@RestController
@RequestMapping("/api/v1/orkestrering")
@RequiredArgsConstructor
public class OrkestreringController {

    private final FlatfileService flatfileService;

    @PostMapping("/generer")
    public ResponseEntity<String> generer(@Valid @RequestBody List<EregDataRequest> data) {
        return ResponseEntity.ok(flatfileService.mapEreg(data, false, ""));
    }

    @PostMapping("/opprett")
    public ResponseEntity<String> opprettEnheterIEreg(@Valid @RequestBody List<EregDataRequest> data, @RequestParam boolean lastOpp, @RequestParam String miljoe) {
        String eregData = flatfileService.mapEreg(data, lastOpp, miljoe);

        if ("".equals(eregData)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(eregData);
    }

    @PostMapping("/flatfil/jenkins")
    public ResponseEntity sendFlatfil(@RequestBody String flatFil, @RequestParam String miljoe) {
        boolean send = flatfileService.sendToJenkins(flatFil, miljoe);
        if (!send) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

}
