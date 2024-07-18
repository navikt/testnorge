package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.JobbService;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.KodeverkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobbController {

    @Autowired
    private final JobbService jobbService;

    private final KodeverkService kodeverkService;

    @GetMapping
    public ResponseEntity<List<JobbParameterEntity>> hentAlleJobber() {
        var jobber = jobbService.hentAlle();
        return ResponseEntity.ok(jobber);
    }

    @GetMapping("/verdiOrganisasjoner")
    public ResponseEntity<List<Integer>> hentVerdierOrganisasjoner(){
        List<Integer> antallOrganisasjoner = new ArrayList<>();
        for(int i =20; i<=500; i+=20){
            antallOrganisasjoner.add(i);
        }
        return ResponseEntity.ok(antallOrganisasjoner);
    }

    @GetMapping("/verdiPersoner")
    public ResponseEntity<List<Integer>> hentVerdierPersoner(){
        List<Integer> antallPersoner = new ArrayList<>();
        for(int i =100; i<=1000; i+=100){
            antallPersoner.add(i);
        }
        return ResponseEntity.ok(antallPersoner);
    }

    @GetMapping("/verdiArbeidstidsOrdning")
    public ResponseEntity<List<String>> hentVerdierArbeidstidsOrdning(){
        List<String> arbeidstidsOrdning = kodeverkService.hentKodeverkValues(KodeverkNavn.ARBEIDSTIDSORDNINGER.value);
        return ResponseEntity.ok(arbeidstidsOrdning);
    }
    @GetMapping("/verdiStillingsprosent")
    public ResponseEntity<List<Integer>> hentVerdierStillingsprosent(){
        List<Integer> stillingsprosent = new ArrayList<>();
        for(int i =20; i<=100; i+=10){
            stillingsprosent.add(i);
        }
        return ResponseEntity.ok(stillingsprosent);
    }

    @PutMapping("/oppdatereVerdier/{parameterNavn}")
    @Operation(description = "Legg inn nye verdier for en parameter")
    public ResponseEntity<JobbParameterEntity> oppdatereVerdier(JobbParameterEntity jobbParameterEntity){
        JobbParameterEntity jobb = jobbService.updateVerdi(jobbParameterEntity);
        return ResponseEntity.ok(jobb);
    }



}
