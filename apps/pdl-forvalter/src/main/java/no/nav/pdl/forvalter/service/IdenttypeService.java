package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO.NyttNavnDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.LocalDate.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.DNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.NPID;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.UKJENT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.GAMMEL_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.NY_IDENTITET;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdenttypeService implements Validation<IdentRequestDTO> {

    private static final LocalDateTime START_OF_ERA = LocalDateTime.of(1900, 1, 1, 0, 0);
    private static final String VALIDATION_DATO_INVALID = "Identtype ugyldig forespørsel: støttet datointervall " +
                                                          "er fødsel mellom 1.1.1900 og dagens dato";
    private static final String VALIDATION_DATO_INTERVAL_INVALID = "Identtype ugyldig forespørsel: fødtFør kan ikke være tidligere " +
                                                                   "enn fødtEtter";
    private static final String VALIDATION_IDENTTYPE_INVALID = "Identtype må være en av FNR, DNR eller BOST";
    private static final String VALIDATION_ALDER_NOT_ALLOWED = "Alder må være mellom 0 og 120 år";

    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final SwopIdentsService swopIdentsService;
    private final MapperFacade mapperFacade;
    private final PersonRepository personRepository;

    public Mono<DbPerson> convert(DbPerson person) {

        var nyPerson = new AtomicReference<>(person);
        var nyeIdenter = mapperFacade.mapAsList(person.getPerson().getNyident(), IdentRequestDTO.class);
        nyeIdenter.forEach(nyIdent -> nyIdent.setIdenttype(
                nonNull(nyIdent.getIdenttype()) ? nyIdent.getIdenttype() : IdenttypeUtility.getIdenttype(person.getIdent())));
        nyeIdenter.sort(sortIdenter());

        return Flux.fromIterable(nyeIdenter)
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person.getPerson()));
                    return handle(type, nyPerson.get())
                            .doOnNext(nyPerson::set);
                })
                .last(nyPerson.get());
    }

    @Override
    public Mono<Void> validate(IdentRequestDTO request) {

        if (nonNull(request.getIdenttype()) && FNR != request.getIdenttype() &&
            DNR != request.getIdenttype() && NPID != request.getIdenttype()) {
            return Mono.error(new InvalidRequestException(VALIDATION_IDENTTYPE_INVALID));
        }

        if (nonNull(request.getFoedtEtter()) && (START_OF_ERA.isAfter(request.getFoedtEtter()) ||
                                                 LocalDateTime.now().isBefore(request.getFoedtEtter()))) {
            return Mono.error(new InvalidRequestException(VALIDATION_DATO_INVALID));
        }

        if (nonNull(request.getFoedtFoer()) && (START_OF_ERA.isAfter(request.getFoedtFoer()) ||
                                                LocalDateTime.now().isBefore(request.getFoedtFoer()))) {
            return Mono.error(new InvalidRequestException(VALIDATION_DATO_INVALID));
        }

        if (nonNull(request.getFoedtEtter()) && nonNull(request.getFoedtFoer()) &&
            request.getFoedtEtter().isAfter(request.getFoedtFoer())) {
            return Mono.error(new InvalidRequestException(VALIDATION_DATO_INTERVAL_INVALID));
        }

        if (nonNull(request.getAlder()) && (request.getAlder() < 0 || request.getAlder() > 120)) {
            return Mono.error(new InvalidRequestException(VALIDATION_ALDER_NOT_ALLOWED));
        }
        return Mono.empty();
    }

    private Mono<DbPerson> handle(IdentRequestDTO request, DbPerson person) {

        person.getPerson().getNavPersonIdentifikator().stream()
                .filter(navIdent -> isNull(navIdent.getGyldigTilOgMed()))
                .forEach(navIdent ->
                        navIdent.setGyldigTilOgMed(now().minusDays(1)));

        return Mono.defer(() -> {
                    if (isNotBlank(request.getEksisterendeIdent())) {

                        return personRepository.findByIdent(request.getEksisterendeIdent())
                                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Eksisterende ident %s ble ikke funnet",
                                        request.getEksisterendeIdent()))));

                    } else {

                        val nyRequest = PersonRequestDTO.builder()
                                .eksisterendeIdent(request.getEksisterendeIdent())
                                .identtype(getIdenttype(request, person.getIdent()))
                                .kjoenn(getKjoenn(request, person.getPerson()))
                                .foedtEtter(getFoedtEtter(request, person.getPerson()))
                                .foedtFoer(getFoedtFoer(request, person.getPerson()))
                                .nyttNavn(mapperFacade.map(request.getNyttNavn(), NyttNavnDTO.class))
                                .syntetisk(isSyntetisk(request, person.getIdent()))
                                .id2032(nonNull(request.getId2032()) ? request.getId2032() : person.getPerson().getId2032())
                                .build();

                        if (nyRequest.getFoedtFoer().isBefore(nyRequest.getFoedtEtter())) {
                            nyRequest.setFoedtFoer(nyRequest.getFoedtEtter().plusDays(3));
                        }
                        return createPersonService.execute(nyRequest);
                    }
                })
                .flatMap(dbPerson -> swopIdentsService.execute(person.getIdent(), dbPerson.getIdent()))
                .flatMap(dbPerson -> relasjonService.setRelasjon(dbPerson.getIdent(), person.getIdent(), NY_IDENTITET)
                        .then(relasjonService.setRelasjon(person.getIdent(), dbPerson.getIdent(), GAMMEL_IDENTITET))
                        .thenReturn(dbPerson));
    }

    private static Identtype getIdenttype(IdentRequestDTO request, String ident) {

        if (nonNull(request.getIdenttype())) {
            return request.getIdenttype();
        }
        return isNotBlank(ident) ? IdenttypeUtility.getIdenttype(ident) : FNR;
    }

    private static Kjoenn getKjoenn(IdentRequestDTO request, PersonDTO person) {

        if (nonNull(request.getKjoenn()) && request.getKjoenn() != UKJENT) {
            return request.getKjoenn();
        }
        return KjoennFraIdentUtility.getKjoenn(person);
    }

    private static LocalDateTime getFoedtFoer(IdentRequestDTO request, PersonDTO person) {

        if (nonNull(request.getFoedtFoer())) {
            return request.getFoedtFoer();

        } else if (nonNull(request.getAlder())) {
            return LocalDateTime.now().minusYears(request.getAlder());
        }
        return FoedselsdatoUtility.getFoedselsdato(person);
    }

    private static LocalDateTime getFoedtEtter(IdentRequestDTO request, PersonDTO person) {

        if (nonNull(request.getFoedtEtter())) {
            return request.getFoedtEtter();

        } else if (nonNull(request.getAlder())) {
            return LocalDateTime.now().minusYears(request.getAlder());
        }
        return FoedselsdatoUtility.getFoedselsdato(person);
    }

    private static boolean isSyntetisk(IdentRequestDTO request, String ident) {

        return nonNull(request.getSyntetisk()) ? request.getSyntetisk() :
                SyntetiskFraIdentUtility.isSyntetisk(ident);
    }

    private Comparator<IdentRequestDTO> sortIdenter() {

        return (ir1, ir2) -> {
            if (ir1.getIdenttype() == (ir2.getIdenttype())) {
                return 0;
            } else if (ir1.getIdenttype() == NPID) {
                return -1;
            } else if (ir2.getIdenttype() == NPID) {
                return 1;
            } else if (ir1.getIdenttype() == DNR && ir2.getIdenttype() == FNR) {
                return -1;
            } else {
                return 1;
            }
        };
    }
}
