package no.nav.levendearbeidsforholdscheduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.levendearbeidsforholdscheduler.domain.StatusRespons;
import no.nav.levendearbeidsforholdscheduler.scheduler.JobbScheduler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static no.nav.levendearbeidsforholdscheduler.utils.Utils.sifferTilHeltall;

@Slf4j
@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class JobbController {

    private final JobbScheduler jobbScheduler;

    /**
     * Request handler funksjon for å restarte scheduler
     * @param  intervall positivt heltall for å representere times-intervall
     * @return respons til klienten for den tilsvarende spørringen
     */
    @GetMapping(value = {"", "/start"})
    public ResponseEntity<String> reschedule(@RequestParam String intervall) {

        if (intervall == null) {
            return ResponseEntity.badRequest().body("intervall er ikke spesifisert");
        }

        var resultat = sifferTilHeltall(intervall);

        if (resultat.isPresent()){
            if (jobbScheduler.startScheduler(resultat.get())){
                return ResponseEntity.ok("Aktivering av scheduler var vellykket med intervall: " + intervall);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.badRequest().body("Intervall er ikke gyldig heltall");
        }
    }

    /**
     * Request handler funksjon som returnerer status på om scheduler kjører for øyeblikket og eventuelt tidspunktet
     * for neste gang scheduleren skal kjøre
     * @return 200 OK, med status- og tidspunkt data på JSON format i response body
     */
    @GetMapping(value = "/status", produces = "application/json")
    public ResponseEntity<String> status() {

        var tidspunkt = Optional.of("");

        boolean status = jobbScheduler.hentStatusKjorer();

        ObjectMapper mapper = new ObjectMapper();
        StatusRespons statusRespons = new StatusRespons();

        statusRespons.setStatus(status);

        if (status){
            tidspunkt = jobbScheduler.hentTidspunktNesteKjoring();
            if (tidspunkt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        statusRespons.setNesteKjoring(tidspunkt.get());

        try {
            String jsonRespons = mapper.writeValueAsString(statusRespons);
            return ResponseEntity.ok(jsonRespons);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Request handler funksjon for å stoppe scheduleren
     * @return true hvis kanselleringen var vellykket
     */
    @GetMapping(value = "/stopp")
    public ResponseEntity<String> stopp() {

        if (!jobbScheduler.stoppScheduler()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } else {
            return ResponseEntity.ok("Deaktivering av scheduler var vellykket");
        }
    }
}

