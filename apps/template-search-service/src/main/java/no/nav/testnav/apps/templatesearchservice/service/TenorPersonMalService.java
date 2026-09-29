package no.nav.testnav.apps.templatesearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.templatesearchservice.consumers.DollyBackendConsumer;
import no.nav.testnav.apps.templatesearchservice.consumers.dto.DollyTeamDTO;
import no.nav.testnav.apps.templatesearchservice.domain.OpprettTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalOwner;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMal;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalBrukerResponse;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalLagreResult;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalOversiktResponse;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalResponse;
import no.nav.testnav.apps.templatesearchservice.domain.ValidertTenorPersonMal;
import no.nav.testnav.apps.templatesearchservice.exception.DollyBackendUnavailableException;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalConflictException;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalNotFoundException;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalValidationException;
import no.nav.testnav.apps.templatesearchservice.repository.TenorPersonMalRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorPersonMalService {

    private static final String ALLE = "ALLE";
    private static final String UKJENT_TEAM = "Ukjent team";
    private static final Comparator<TenorPersonMal> MAL_COMPARATOR =
            Comparator.comparing(TenorPersonMal::getMalNavn, CASE_INSENSITIVE_ORDER)
                    .thenComparing(TenorPersonMal::getId);
    private static final Comparator<TenorPersonMalBrukerResponse> BRUKER_COMPARATOR =
            Comparator.comparing(TenorPersonMalBrukerResponse::brukernavn, CASE_INSENSITIVE_ORDER)
                    .thenComparing(TenorPersonMalBrukerResponse::brukerId);

    private final CurrentTenorUserService currentUserService;
    private final TenorMalAccessService accessService;
    private final DollyBackendConsumer dollyBackendConsumer;
    private final TenorPersonMalValidationService validationService;
    private final TenorPersonMalRepository malRepository;
    private final JsonMapper jsonMapper;

    public Mono<TenorPersonMalLagreResult> save(OpprettTenorPersonMalRequest request) {
        return Mono.fromSupplier(() -> validationService.validate(request))
                .flatMap(validertMal -> currentUserService.getCurrentUser()
                        .flatMap(owner -> save(owner, validertMal)));
    }

    public Flux<TenorPersonMalResponse> getMaler(String brukerId) {
        return currentUserService.getAuthenticatedUser()
                .flatMapMany(currentUser -> accessService.getAccessibleMaler(currentUser)
                        .filter(mal -> isRequestedOwner(currentUser, brukerId, mal))
                        .sort(MAL_COMPARATOR)
                        .map(this::toResponse));
    }

    public Mono<TenorPersonMalOversiktResponse> getMalOversikt() {
        return currentUserService.getAuthenticatedUser()
                .flatMap(currentUser -> accessService.getAccessibleMaler(currentUser)
                        .sort(Comparator.comparing(TenorPersonMal::getSistOppdatert).reversed())
                        .distinct(TenorPersonMal::getBrukerId)
                        .collectList()
                        .flatMap(this::getBrukereMedMaler)
                        .map(brukere -> new TenorPersonMalOversiktResponse(
                                withAllUsersOption(currentUser, brukere))));
    }

    public Mono<TenorPersonMalResponse> updateMalNavn(Long id, String malNavn) {
        return Mono.fromSupplier(() -> validationService.validateMalNavn(malNavn))
                .flatMap(validertMalNavn -> currentUserService.getCurrentUser()
                        .flatMap(owner -> updateMalNavn(id, owner, validertMalNavn)));
    }

    public Mono<Void> delete(Long id) {
        return currentUserService.getCurrentUser()
                .flatMap(owner -> malRepository.deleteByIdAndBrukerId(id, owner.brukerId()))
                .filter(deletedRows -> deletedRows > 0)
                .switchIfEmpty(Mono.error(new TenorMalNotFoundException("Malen ble ikke funnet.")))
                .then();
    }

    private Mono<List<TenorPersonMalBrukerResponse>> getBrukereMedMaler(List<TenorPersonMal> maler) {
        Mono<List<DollyTeamDTO>> teamOppslag = maler.stream()
                .anyMatch(mal -> mal.getBrukertype() == TenorMalBrukerType.TEAM)
                ? dollyBackendConsumer.getTeams()
                : Mono.just(List.of());

        return teamOppslag
                .onErrorResume(DollyBackendUnavailableException.class, _ -> {
                    log.warn("Kunne ikke hente teamnavn fra Dolly. Viser ukjent team.");
                    return Mono.just(List.of());
                })
                .map(teams -> {
                    var teamNavn = new HashMap<String, String>();
                    teams.forEach(team -> teamNavn.put(team.brukerId(), team.navn()));
                    return maler.stream()
                            .map(mal -> new TenorPersonMalBrukerResponse(
                                    mal.getBrukerId(),
                                    getBrukernavn(mal, teamNavn)))
                            .sorted(BRUKER_COMPARATOR)
                            .toList();
                });
    }

    private String getBrukernavn(TenorPersonMal mal, Map<String, String> teamNavn) {
        if (mal.getBrukertype() != TenorMalBrukerType.TEAM) {
            return mal.getBrukernavn();
        }
        var navn = teamNavn.get(mal.getBrukerId());
        if (isBlank(navn)) {
            log.warn("Teamnavn mangler i svaret fra Dolly. Viser ukjent team.");
            return UKJENT_TEAM;
        }
        try {
            validationService.validateNoPersonidentifikator(navn);
            return navn;
        } catch (TenorMalValidationException _) {
            log.warn("Teamnavn inneholder en personidentifikator. Viser ukjent team.");
            return UKJENT_TEAM;
        }
    }

    private Mono<TenorPersonMalLagreResult> save(
            TenorMalOwner owner,
            ValidertTenorPersonMal validertMal
    ) {
        return malRepository.findByBrukerIdAndMalNavnIgnoreCase(
                        owner.brukerId(),
                        validertMal.malNavn())
                .flatMap(existingMal -> saveExistingMal(existingMal, owner, validertMal))
                .switchIfEmpty(Mono.defer(() -> saveNewMal(owner, validertMal)))
                .onErrorResume(DataIntegrityViolationException.class, exception ->
                        malRepository.findByBrukerIdAndMalNavnIgnoreCase(
                                        owner.brukerId(),
                                        validertMal.malNavn())
                                .switchIfEmpty(Mono.error(exception))
                                .flatMap(existingMal -> saveExistingMal(existingMal, owner, validertMal)));
    }

    private Mono<TenorPersonMalLagreResult> saveExistingMal(
            TenorPersonMal existingMal,
            TenorMalOwner owner,
            ValidertTenorPersonMal validertMal
    ) {
        existingMal.setMalNavn(validertMal.malNavn());
        existingMal.setSoekKriterier(validertMal.soekKriterier());
        existingMal.setBrukernavn(owner.brukernavn());
        existingMal.setBrukertype(owner.brukertype());
        existingMal.setSistOppdatert(Instant.now());
        return malRepository.save(existingMal)
                .map(mal -> new TenorPersonMalLagreResult(toResponse(mal), false));
    }

    private Mono<TenorPersonMalLagreResult> saveNewMal(
            TenorMalOwner owner,
            ValidertTenorPersonMal validertMal
    ) {
        var now = Instant.now();
        var mal = TenorPersonMal.builder()
                .malNavn(validertMal.malNavn())
                .soekKriterier(validertMal.soekKriterier())
                .brukerId(owner.brukerId())
                .brukernavn(owner.brukernavn())
                .brukertype(owner.brukertype())
                .opprettet(now)
                .sistOppdatert(now)
                .build();
        return malRepository.save(mal)
                .map(savedMal -> new TenorPersonMalLagreResult(toResponse(savedMal), true));
    }

    private Mono<TenorPersonMalResponse> updateMalNavn(
            Long id,
            TenorMalOwner owner,
            String malNavn
    ) {
        return malRepository.findByIdAndBrukerId(id, owner.brukerId())
                .switchIfEmpty(Mono.error(new TenorMalNotFoundException("Malen ble ikke funnet.")))
                .map(mal -> rename(mal, owner, malNavn))
                .flatMap(malRepository::save)
                .map(this::toResponse)
                .onErrorMap(
                        DataIntegrityViolationException.class,
                        _ -> new TenorMalConflictException("En mal med dette navnet finnes allerede."));
    }

    private static TenorPersonMal rename(
            TenorPersonMal mal,
            TenorMalOwner owner,
            String malNavn
    ) {
        mal.setMalNavn(malNavn);
        mal.setBrukernavn(owner.brukernavn());
        mal.setBrukertype(owner.brukertype());
        mal.setSistOppdatert(Instant.now());
        return mal;
    }

    private static boolean isRequestedOwner(
            TenorMalOwner currentUser,
            String brukerId,
            TenorPersonMal mal
    ) {
        if (ALLE.equals(brukerId)) {
            return isAzureOrTeam(currentUser);
        }
        return mal.getBrukerId().equals(brukerId);
    }

    private static List<TenorPersonMalBrukerResponse> withAllUsersOption(
            TenorMalOwner currentUser,
            List<TenorPersonMalBrukerResponse> brukere
    ) {
        if (!isAzureOrTeam(currentUser)) {
            return brukere;
        }
        return Stream.concat(
                        Stream.of(new TenorPersonMalBrukerResponse(ALLE, ALLE)),
                        brukere.stream())
                .toList();
    }

    private static boolean isAzureOrTeam(TenorMalOwner user) {
        return user.brukertype() == TenorMalBrukerType.AZURE ||
                user.brukertype() == TenorMalBrukerType.TEAM;
    }

    private TenorPersonMalResponse toResponse(TenorPersonMal mal) {
        try {
            return new TenorPersonMalResponse(
                    mal.getId(),
                    mal.getMalNavn(),
                    jsonMapper.readTree(mal.getSoekKriterier()),
                    mal.getOpprettet(),
                    mal.getSistOppdatert());
        } catch (JacksonException exception) {
            throw new IllegalStateException("Kunne ikke lese lagrede søkekriterier.", exception);
        }
    }

}
