package no.nav.registre.ereg.provider.rs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.ereg.util.OrgnummerUtil;

@RestController
@RequestMapping("/api/orgnr")
public class OrgnrController {

    @GetMapping
    public ResponseEntity<List<String>> genererOrgnr(@RequestParam int antall) {
        List<String> orgnr = new ArrayList<>(antall);
        for (int i = 0; i < antall; i++) {
            orgnr.add(OrgnummerUtil.generate(new RestTemplate()));
        }
        return ResponseEntity.ok(orgnr);
    }

}
