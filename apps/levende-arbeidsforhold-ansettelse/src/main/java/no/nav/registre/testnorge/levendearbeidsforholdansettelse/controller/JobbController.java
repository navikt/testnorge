package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.AnsettelseService;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.JobbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobbController {

    @Autowired
    private final JobbService jobbService;
    private final AnsettelseService ansettelseService;

    /**
     * Endepunktet som blir benyttet av frontend for å hente alle parameterene og verdiene
     * @return returnerer parameterne
     */
    @GetMapping
    @Operation(description = "Henter alle parametere med verdier")
    public ResponseEntity<List<JobbParameter>> hentAlleJobber() {
        try {
            return ResponseEntity.ok(jobbService.hentAlleParametere());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatusCode.valueOf(Integer.parseInt(String.valueOf(e)))).body(null);
        }
    }

    /**
     * Endepunktet som testnav-levende-arbeidsforhold-scheduler sender spørring til for å
     * starte jobb.
     * @return
     */
    @GetMapping("/ansettelse-jobb")
    public ResponseEntity<String> ansettelseJobb(){
        try {
            ansettelseService.runAnsettelseService();
            return ResponseEntity.ok("Kjørte ansettelse-service");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatusCode.valueOf(Integer.parseInt(String.valueOf(e)))).body(null);
        }
    }

    /**
     * Endepunktet frontenden bruker for å opdatere gjeldende verdi i jobb_parameter db.
     * @param parameterNavn navnet på parameteren som skal bli oppdatert
     * @param verdi ER den nye verdien som skal bli oppdatert
     * @return Enten den nye jobbParameterEntity eller feilmeldingen.
     */
    @PutMapping("/oppdatereVerdier/{parameterNavn}")
    @Operation(description = "Legg inn nye verdier for en parameter")
    public ResponseEntity<JobbParameter> oppdatereVerdier(@PathVariable("parameterNavn") String parameterNavn, @RequestBody String verdi){
        try {
            JobbParameter jobbParameter = jobbService.hentJobbParameter(parameterNavn);
            verdi = verdi.replace("\"", "");
            jobbParameter.setVerdi(verdi);
            return ResponseEntity.ok(jobbService.updateVerdi(jobbParameter));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatusCode.valueOf(Integer.parseInt(String.valueOf(e)))).body(null);
        }
    }
}
