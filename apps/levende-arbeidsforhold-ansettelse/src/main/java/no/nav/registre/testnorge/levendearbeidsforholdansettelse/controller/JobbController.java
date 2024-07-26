package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.AnsettelseService;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.JobbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobbController {

    @Autowired
    private final JobbService jobbService;

    private final AnsettelseService ansettelseService;

    @GetMapping
    public ResponseEntity<List<JobbParameter>> hentAlleJobber() {
        return ResponseEntity.ok(jobbService.hentAlleParametere());
    }
    /*
    @GetMapping("/hentParameterVerdier")
    public ResponseEntity<List<JobbParameterVerdier>> hentAlleJobber2(){
        return ResponseEntity.ok(jobbService.hentAlleParametereMedVerdier());
    }

     */



    @GetMapping("/verdi/{parameterNavn}")
    public ResponseEntity<List<String>> hentVerdier(@PathVariable("parameterNavn") String parameternavn) {
        JobbParameter jobbParameter = jobbService.hentJobbParameter(parameternavn);
        return ResponseEntity.ok(jobbService.finnAlleVerdier(jobbParameter));
    }

    @PutMapping("/oppdatereVerdier/{parameterNavn}")
    @Operation(description = "Legg inn nye verdier for en parameter")
    public ResponseEntity<JobbParameter> oppdatereVerdier(@PathVariable("parameterNavn") String parameterNavn, @RequestBody String verdi){
        log.info("Fått PUT-request parameternavn: {}, verdi: {}", parameterNavn, verdi);

        JobbParameter jobbParameter = jobbService.hentJobbParameter(parameterNavn);
        verdi = verdi.replace("\"","");
        log.info("Jobbparameter: {}", jobbParameter.getVerdi() );
        jobbParameter.setVerdi(verdi);
        log.info("verdi satt {}", jobbParameter.getVerdi());
        return ResponseEntity.ok(jobbService.updateVerdi(jobbParameter));
    }



}
