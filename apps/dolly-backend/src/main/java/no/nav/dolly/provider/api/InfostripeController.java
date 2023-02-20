package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.InfoStripe;
import no.nav.dolly.domain.resultset.entity.infostripe.RsInfostripeMelding;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.InformasjonsmeldingRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/infostripe", produces = MediaType.APPLICATION_JSON_VALUE)
public class InfostripeController {

    private final InformasjonsmeldingRepository informasjonsmeldingRepository;
    private final MapperFacade mapperFacade;

    @GetMapping()
    @Operation(description = "Hent alle gyldige informasjons meldinger")
    public Collection<RsInfostripeMelding> hentAlle() {
        return informasjonsmeldingRepository.findGyldigMeldinger().stream()
                .map(melding -> mapperFacade.map(melding, RsInfostripeMelding.class))
                .toList();
    }

    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(description = "Oppret ny informasjons melding")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public String oppretNyMelding(@RequestBody RsInfostripeMelding melding) {
        var infostripeMelding = mapperFacade.map(melding, InfoStripe.class);
        informasjonsmeldingRepository.save(infostripeMelding);
        return infostripeMelding.getId().toString();
    }

    @PutMapping("{id}")
    @Operation(description = "Oppdater informasjons melding")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void oppdaterMeldig(@PathVariable("id") Long id, @RequestBody RsInfostripeMelding melding) {
        var infostripeMelding = informasjonsmeldingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id + " finnes ikke"));

        infostripeMelding.setType(melding.getType());
        infostripeMelding.setMessage(melding.getMessage());
        infostripeMelding.setStart(melding.getStart());
        infostripeMelding.setExpires(melding.getExpires());
        informasjonsmeldingRepository.save(infostripeMelding);
    }

    @DeleteMapping("{id}")
    @Operation(description = "Slett informasjons melding")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void slettMelding(@PathVariable("id") Long id) {
        try {
            informasjonsmeldingRepository.deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException(id + " finnes ikke");
        }
    }
}

