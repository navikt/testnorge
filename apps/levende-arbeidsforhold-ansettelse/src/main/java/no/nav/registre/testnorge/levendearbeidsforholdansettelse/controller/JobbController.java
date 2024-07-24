package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.VerdierEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.AnsettelseService;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.JobbService;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.KodeverkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobbController {

    @Autowired
    private final JobbService jobbService;

    private final AnsettelseService ansettelseService;

    @GetMapping
    public ResponseEntity<List<JobbParameterEntity>> hentAlleJobber() {
        /*
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "http://localhost:3000");
        responseHeaders.set("Access-Control-Allow-Methods", "GET, POST, PUT");
        responseHeaders.set("Access-Control-Allow-Headers", "Authorization, Content-Type");

         */
        //return ResponseEntity.ok().headers(responseHeaders).body(jobbService.hentAlleParametere());
        //jobbService.initDb();
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
    public List<VerdierEntity> hentVerdier(@PathVariable("parameterNavn") String parameternavn) {
        return jobbService.hentAlleMedNavn(parameternavn);
    }

    @PutMapping("/oppdatereVerdier/{parameterNavn}")
    @Operation(description = "Legg inn nye verdier for en parameter")
    public ResponseEntity<JobbParameterEntity> oppdatereVerdier(JobbParameterEntity jobbParameterEntity){
        JobbParameterEntity jobb = jobbService.updateVerdi(jobbParameterEntity);
        return ResponseEntity.ok(jobb);
    }

    @GetMapping("/kjor")
    public void kjor(){
        ansettelseService.runAnsettelseService();
    }


}
