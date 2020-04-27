package no.nav.brregstub.rs;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.RolleKode;
import no.nav.brregstub.api.UnderstatusKode;
import no.nav.brregstub.service.HentRolleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/kode")
@AllArgsConstructor
public class KodeController {

    private final HentRolleService service;


    @GetMapping("/roller")
    public ResponseEntity<EnumMap<RolleKode, String>> hentRollekoder() {
        var returValue = new EnumMap<RolleKode, String>(RolleKode.class);
        for (RolleKode rolleKode : RolleKode.values()) {
            returValue.put(rolleKode, rolleKode.getBeskrivelse());
        }

        return ResponseEntity.status(HttpStatus.OK).body(returValue);
    }

    @GetMapping("/understatus")
    public ResponseEntity<Map<Integer, String>> hentUnderstatuskoder() {
        return ResponseEntity.status(HttpStatus.OK).body(UnderstatusKode.understatusKoder);
    }

}
