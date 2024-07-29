package no.nav.levendearbeidsforholdscheduler.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import no.nav.levendearbeidsforholdscheduler.scheduler.JobbScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.levendearbeidsforholdscheduler.utils.Utils.intervallErHeltall;
import static no.nav.levendearbeidsforholdscheduler.utils.Utils.lagCronExpression;

@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class JobbController {

    private final JobbScheduler jobbScheduler;

    /**
     * Request handler funksjon for å restarte scheduler
     * @param request Spørrings-objekt fra klient
     * @return respons til klienten for den tilsvarende spørringen
     */
    @GetMapping
    public ResponseEntity<String> reschedule(HttpServletRequest request) {
        String intervall = request.getHeader("intervall");

        if (intervall == null) {
            return ResponseEntity.badRequest().body("intervall er ikke spesifisert");
        }

        String cronExpression;
        boolean erGyldigHeltall = intervallErHeltall(intervall);

        if (erGyldigHeltall){
            cronExpression = lagCronExpression(intervall);
        } else {
            return ResponseEntity.badRequest().body("intervall er ikke gyldig heltall");
        }

        jobbScheduler.startScheduler(cronExpression);
        return ResponseEntity.ok("Scheduler har begynt med intervall " + intervall);
    }

    //TODO: Lage et endepunkt som svarer på om scheduler kjører for øyeblikket

}
