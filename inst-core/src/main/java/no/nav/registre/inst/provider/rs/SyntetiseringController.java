package no.nav.registre.inst.provider.rs;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.inst.service.InstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private InstService instService;

    @LogExceptions
    @PostMapping(value = "/generer")
    public void generateInst(@RequestParam int numToGenerate) {
        List<Map<String, String>> instMeldinger = instService.finnSyntetiserteMeldinger(numToGenerate);
        System.out.println(instMeldinger);
    }
}