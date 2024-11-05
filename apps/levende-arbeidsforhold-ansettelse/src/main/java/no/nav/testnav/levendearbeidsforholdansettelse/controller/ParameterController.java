package no.nav.testnav.levendearbeidsforholdansettelse.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.ParameterDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.testnav.levendearbeidsforholdansettelse.service.ParameterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/parameter")
@RequiredArgsConstructor
public class ParameterController {

    private final ParameterService parameterService;

    /**
     * Endepunktet som blir benyttet av frontend for å hente alle parameterene og verdiene
     * @return returnerer parameterne
     */
    @GetMapping
    @Operation(description = "Henter alle parametre for oppretting av arbeidsforhold")
    public Flux<ParameterDTO> hentAlleParametere() {

            return parameterService.hentAlleParametere();
    }

    /**
     * Endepunktet frontenden bruker for å opdatere gjeldende verdi i jobb_parameter db.
     * @param parameternavn navnet på parameteren som skal bli oppdatert
     * @param verdi ER den nye verdien som skal bli oppdatert
     */
    @PutMapping("/{parameternavn}")
    @Operation(description = "Legg inn ny verdi på en parameter")
    @ResponseStatus(HttpStatus.OK)
    public Mono<JobbParameter> oppdatereVerdier(@PathVariable("parameternavn") String parameternavn, @RequestBody String verdi){

            return parameterService.updateVerdi(parameternavn, verdi);
    }
}
