package no.nav.testnav.levendearbeidsforholdscheduler.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdscheduler.domain.StatusRespons;
import no.nav.testnav.levendearbeidsforholdscheduler.scheduler.JobbScheduler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static no.nav.testnav.levendearbeidsforholdscheduler.utils.Utils.sifferTilHeltall;

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
    @Operation(description = "Starter scheduleren med en forsinkelse av et gitt intervall på x antall timer")
    public ResponseEntity<String> reschedule(@Schema(description = "Positivt heltall for å representere times-intervall") @RequestParam String intervall) {

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
     * for neste gang scheduleren skal kjøre ansettelses-jobben
     * @return 200 OK, med status- og tidspunkt data på JSON format i response body
     */
    @GetMapping(value = "/status", produces = "application/json")
    @Operation(description = "Henter statusen på om scheduleren er aktiv eller ikke. Dersom den er aktiv, vil den også returnere tidspunktet for neste gang scheduleren skal kjøre ansettelse jobben")
    public ResponseEntity<StatusRespons> status() {

        var tidspunkt = Optional.of("");

        var status = jobbScheduler.hentStatusKjorer();

        var statusRespons = new StatusRespons();

        statusRespons.setStatus(status);

        if (status){
            tidspunkt = jobbScheduler.hentTidspunktNesteKjoring();
            if (tidspunkt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        statusRespons.setNesteKjoring(tidspunkt.get());

        return ResponseEntity.ok(statusRespons);
    }

    /**
     * Request handler funksjon for å stoppe scheduleren
     * @return true hvis kanselleringen var vellykket
     */
    @GetMapping(value = "/stopp")
    @Operation(description = "Stopper scheduleren dersom den er aktiv")
    public ResponseEntity<String> stopp() {

        if (!jobbScheduler.stoppScheduler()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } else {
            return ResponseEntity.ok("Deaktivering av scheduler var vellykket");
        }
    }
}

