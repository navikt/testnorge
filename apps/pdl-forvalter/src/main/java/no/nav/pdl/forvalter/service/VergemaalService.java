package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class VergemaalService implements Validation<VergemaalDTO> {

    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_EMBETE_ERROR = "Embete for vergemål må angis";
    private static final String VALIDATION_TYPE_ERROR = "Sakstype av vergemål må angis";
    private static final String VALIDATION_VERGEMAAL_ERROR = "Vergeperson med ident %s ikke funnet i database";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getVergemaal())
                .filter(vergemaal -> isTrue(vergemaal.getIsNew()))
                .flatMap(vergemaal -> handle(vergemaal, person.getIdent()))
                .doOnNext(vergemaal -> {
                    vergemaal.setKilde(getKilde(vergemaal));
                    vergemaal.setMaster(getMaster(vergemaal, person));
                })
                .collectList()
                .doOnNext(vergemaal -> person.setVergemaal(new ArrayList<>(vergemaal)))
                .then();
    }

    public Mono<Void> validate(VergemaalDTO vergemaal) {

        if (isNull(vergemaal.getVergemaalEmbete())) {
            throw new InvalidRequestException(VALIDATION_EMBETE_ERROR);
        }

        if (nonNull(vergemaal.getGyldigFraOgMed()) && nonNull(vergemaal.getGyldigTilOgMed()) &&
            !vergemaal.getGyldigFraOgMed().isBefore(vergemaal.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }

        if (isNull(vergemaal.getSakType())) {
            throw new InvalidRequestException(VALIDATION_TYPE_ERROR);
        }

        if (isNotBlank(vergemaal.getVergeIdent())) {
            return personRepository.existsByIdent(vergemaal.getVergeIdent())
                    .flatMap(exists -> isFalse(exists) ?
                            Mono.error(new InvalidRequestException(format(VALIDATION_VERGEMAAL_ERROR, vergemaal.getVergeIdent()))) :
                            Mono.empty());
        }
        return Mono.empty();
    }

    private Mono<VergemaalDTO> handle(VergemaalDTO vergemaal, String ident) {

        return getVergeident(vergemaal, ident)
                .flatMap(vm ->
                        relasjonService.setRelasjoner(ident, RelasjonType.VERGE_MOTTAKER,
                                        vm.getVergeIdent(), RelasjonType.VERGE)
                                .thenReturn(vergemaal));
    }

    private Mono<VergemaalDTO> getVergeident(VergemaalDTO vergemaal, String ident) {

        vergemaal.setEksisterendePerson(isNotBlank(vergemaal.getVergeIdent()));

        if (isBlank(vergemaal.getVergeIdent())) {

            if (isNull(vergemaal.getNyVergeIdent())) {
                vergemaal.setNyVergeIdent(new PersonRequestDTO());
            }

            if (isNull(vergemaal.getNyVergeIdent().getAlder()) &&
                isNull(vergemaal.getNyVergeIdent().getFoedtEtter()) &&
                isNull(vergemaal.getNyVergeIdent().getFoedtFoer())) {

                vergemaal.getNyVergeIdent().setFoedtFoer(LocalDateTime.now().minusYears(18));
                vergemaal.getNyVergeIdent().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            EgenskaperFraHovedperson.kopierData(ident, vergemaal.getNyVergeIdent());

            return createPersonService.execute(vergemaal.getNyVergeIdent())
                    .map(createdPerson -> {
                        vergemaal.setVergeIdent(createdPerson.getIdent());
                        return vergemaal;
                    });
        }
        return Mono.just(vergemaal);
    }
}