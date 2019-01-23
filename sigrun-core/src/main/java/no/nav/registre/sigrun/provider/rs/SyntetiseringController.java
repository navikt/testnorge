package no.nav.registre.sigrun.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import no.nav.registre.sigrun.service.PoppService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    PoppService poppService;

    @PostMapping(value = "/generatePopp")
    public ResponseEntity generatePopp(@RequestBody String[] fnrs) throws InterruptedException, ExecutionException {
        List<Map<String, Object>> result = poppService.getPoppMeldinger(fnrs);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
