package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.InfoStripe;
import no.nav.dolly.domain.resultset.entity.infostripe.InfostripeMelding;
import no.nav.dolly.domain.resultset.entity.infostripe.RsInfostripeMelding;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.InformasjonsmeldingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/infostripe", produces = MediaType.APPLICATION_JSON_VALUE)
public class InfostripeController {

    private final InformasjonsmeldingRepository informasjonsmeldingRepository;
    private final MapperFacade mapperFacade;

    @GetMapping
    @Operation(description = "Hent alle gjeldende informasjonsmeldinger")
    public Flux<InfostripeMelding> hentAlle(@RequestParam(required = false, defaultValue = "false", name = "inkluderFremtidige") boolean inkluderFremtidige) {

        return isTrue(inkluderFremtidige) ?
                informasjonsmeldingRepository.findGjeldendeOgFremtidigeMeldinger()
                        .map(melding -> mapperFacade.map(melding, InfostripeMelding.class)) :
                informasjonsmeldingRepository.findGjeldendeMeldinger()
                        .map(melding -> mapperFacade.map(melding, InfostripeMelding.class));
    }

    @PostMapping
    @Operation(description = "Opprett ny informasjonsmelding")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Mono<InfoStripe> opprettNyMelding(@RequestBody RsInfostripeMelding melding) {

        return Mono.just(mapperFacade.map(melding, InfoStripe.class))
                .flatMap(informasjonsmeldingRepository::save);
    }

    @PutMapping("{id}")
    @Operation(description = "Oppdater informasjonsmelding")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public Mono<InfoStripe> oppdaterMeldig(@PathVariable("id") Long id, @RequestBody InfostripeMelding melding) {

        return informasjonsmeldingRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(id + " finnes ikke")))
                .map(infostripe -> {
                    infostripe.setType(melding.getType());
                    infostripe.setMessage(melding.getMessage());
                    infostripe.setStart(melding.getStart());
                    infostripe.setExpires(melding.getExpires());
                    return infostripe;
                })
                .flatMap(informasjonsmeldingRepository::save);
    }

    @DeleteMapping("{id}")
    @Operation(description = "Slett informasjonsmelding")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public Mono<Void> slettMelding(@PathVariable("id") Long id) {

        return informasjonsmeldingRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(id + " finnes ikke")))
                .flatMap(infostripe -> informasjonsmeldingRepository.deleteById(id));
    }
}

