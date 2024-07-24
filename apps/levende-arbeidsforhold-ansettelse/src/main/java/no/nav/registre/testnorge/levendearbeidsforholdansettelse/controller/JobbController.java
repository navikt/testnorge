package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.Verdier;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.AnsettelseService;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.JobbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobbController {

    @Autowired
    private final JobbService jobbService;

    private final AnsettelseService ansettelseService;

    @GetMapping
    public ResponseEntity<List<JobbParameter>> hentAlleJobber() {


        //return ResponseEntity.ok().headers(responseHeaders).body(jobbService.hentAlleParametere());
        jobbService.initDb();
        return ResponseEntity.ok(jobbService.hentAlleParametere());
    }
/*
    @PostMapping("/parameter/ny")
    public ResponseEntity<JobbParameterEntity> lagParameter(@RequestParam JobbParameterEntity jobbParameterEntity) {
        jobbService.lagreParameter(jobbParameterEntity);
        return ResponseEntity.ok(jobbParameterEntity);
    }
*/
    @GetMapping("/verdi/{parameterNavn}")
    public List<Verdier> hentVerdier(@PathVariable("parameterNavn") String parameternavn) {
        JobbParameter jobbParameter = jobbService.hentJobbParameter(parameternavn);
        return jobbService.hentAlleMedNavn(jobbParameter);
    }

    @PutMapping("/oppdatereVerdier/{parameterNavn}")
    @Operation(description = "Legg inn nye verdier for en parameter")
    public ResponseEntity<JobbParameter> oppdatereVerdier(@PathVariable("parameterNavn") String parameterNavn ){
        JobbParameter jobbParameter = jobbService.hentJobbParameter(parameterNavn);
        return ResponseEntity.ok(jobbService.updateVerdi(jobbParameter));
    }

    @GetMapping("/kjor")
    public void kjor(){
        ansettelseService.runAnsettelseService();
    }


}
