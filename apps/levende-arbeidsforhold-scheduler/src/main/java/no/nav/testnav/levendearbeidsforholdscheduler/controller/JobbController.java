package no.nav.testnav.levendearbeidsforholdscheduler.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdscheduler.domain.StatusRespons;
import no.nav.testnav.levendearbeidsforholdscheduler.scheduler.JobbScheduler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping("/api/v1/scheduler")
@RequiredArgsConstructor
public class JobbController {

    private final JobbScheduler jobbScheduler;

    /**
     * Request handler funksjon for å restarte scheduler
     *
     * @param intervall positivt heltall for å representere times-intervall
     * @return respons til klienten for den tilsvarende spørringen
     */
    @PutMapping("/start")
    @Operation(description = "Starter scheduleren med en forsinkelse av et gitt intervall på x antall timer")
    public Mono<String> reschedule(@RequestParam Integer intervall) {

        if (isNull(intervall)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "intervall er ikke spesifisert");
        }

        jobbScheduler.startScheduler(intervall);

        return Mono.just("Aktivering av scheduler var vellykket med intervall: %d".formatted(intervall));
    }

    /**
     * Request handler funksjon som returnerer status på om scheduler kjører for øyeblikket og eventuelt tidspunktet
     * for neste gang scheduleren skal kjøre ansettelses-jobben
     *
     * @return 200 OK, med status- og tidspunkt data på JSON format i response body
     */
    @GetMapping(value = "/status", produces = "application/json")
    @Operation(description = "Henter statusen på om scheduleren er aktiv eller ikke. Dersom den er aktiv, vil den også returnere tidspunktet for neste gang scheduleren skal kjøre ansettelse jobben")
    public Mono<StatusRespons> status() {

        var tidspunkt = Optional.of("");

        var status = jobbScheduler.hentStatusKjorer();

        var statusRespons = new StatusRespons();

        statusRespons.setStatus(status);

        if (status) {
            tidspunkt = jobbScheduler.hentTidspunktNesteKjoring();
            if (tidspunkt.isEmpty()) {
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        }

        statusRespons.setNesteKjoring(tidspunkt.get());

        return Mono.just(statusRespons);
    }

    /**
     * Request handler funksjon for å stoppe scheduleren
     *
     * @return true hvis kanselleringen var vellykket
     */
    @PutMapping("/stopp")
    @Operation(description = "Stopper scheduleren dersom den er aktiv")
    public Mono<String> stopp() {

        if (!jobbScheduler.stoppScheduler()) {
            return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        } else {
            return Mono.just("Deaktivering av scheduler var vellykket");
        }
    }
}
