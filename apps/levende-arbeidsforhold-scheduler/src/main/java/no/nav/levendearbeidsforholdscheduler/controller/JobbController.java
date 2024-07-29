package no.nav.levendearbeidsforholdscheduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.levendearbeidsforholdscheduler.domain.StatusRespons;
import no.nav.levendearbeidsforholdscheduler.scheduler.JobbScheduler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static no.nav.levendearbeidsforholdscheduler.utils.Utils.intervallErHeltall;
import static no.nav.levendearbeidsforholdscheduler.utils.Utils.lagCronExpression;

@Slf4j
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

    //TODO: Gjøre om endepunkt til å returnere med klokkeslettet neste jobb skal kjøre
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
                return ResponseEntity.internalServerError().body("Noe skjedde galt med å hente ut tidspunkt for neste kjøring");
            }
        }

        statusRespons.setNesteKjoring(tidspunkt.get());

        try {
            String jsonRespons = mapper.writeValueAsString(statusRespons);
            return ResponseEntity.ok(jsonRespons);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("Noe skjedde galt med formattering til JSON");
        }
    }
}
