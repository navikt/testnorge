package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.AVDOEDD_FOR_KONTAKT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FULLMAKTSGIVER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FULLMEKTIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.KONTAKT_FOR_DOEDSBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE_MOTTAKER;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class UnhookEksternePersonerService {

    private final PersonRepository personRepository;
    private final RelasjonRepository relasjonRepository;

    public Mono<Void> unhook(DbPerson hovedperson) {

        return Flux.concat(deleteSivilstandArtifact(hovedperson),
                        deleteForeldreansvarRelasjoner(hovedperson),
                        deleteForelderBarnRelasjoner(hovedperson),
                        deleteFullmaktRelasjoner(hovedperson),
                        deleteVergemaalRelasjoner(hovedperson),
                        deleteKontaktinformasjonForDoedsboeAndreRelasjoner(hovedperson))
                .reduce(new HashSet<DbPerson>(), (acc, set) -> {
                    acc.addAll(set);
                    return acc;
                })
                .flatMap(standalonePartnere ->
                        deleteStandaloneRelasjoner(hovedperson, standalonePartnere))
                .then();
    }

    private Mono<Set<DbPerson>> deleteSivilstandArtifact(DbPerson hovedperson) {

        val standalonePartnere = new AtomicReference<>(new HashSet<DbPerson>());

        return relasjonRepository.findByPersonIdAndRelasjonTypeIn(hovedperson.getId(), List.of(RelasjonType.EKTEFELLE_PARTNER))
                .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                .doOnNext(dbPerson -> {
                    if (isTrue(dbPerson.getPerson().getStandalone())) {
                        standalonePartnere.get().add(dbPerson);
                    }
                })
                .flatMap(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getSivilstand())
                        .doOnNext(sivilstand -> {
                            if (sivilstand.isEksisterendePerson()) {
                                standalonePartnere.get().add(dbPerson);
                            }
                        })
                        .filter(sivilstand ->
                                !Objects.equals(hovedperson.getIdent(), sivilstand.getRelatertVedSivilstand()))
                        .collectList()
                        .doOnNext(sivilstand -> dbPerson.getPerson().setSivilstand(sivilstand))
                        .thenReturn(dbPerson))
                .flatMap(personRepository::save)
                .then()
                .thenReturn(standalonePartnere.get());
    }

    private Mono<Set<DbPerson>> deleteForelderBarnRelasjoner(DbPerson hovedperson) {

        val standalonePartnere = new AtomicReference<>(new HashSet<DbPerson>());

        return relasjonRepository.findByPersonIdAndRelasjonTypeIn(hovedperson.getId(), List.of(FAMILIERELASJON_FORELDER, FAMILIERELASJON_BARN))
                .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                .doOnNext(dbPerson -> {
                    if (isTrue(dbPerson.getPerson().getStandalone())) {
                        standalonePartnere.get().add(dbPerson);
                    }
                })
                .flatMap(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getForelderBarnRelasjon())
                        .doOnNext(relasjon -> {
                            if (relasjon.isEksisterendePerson()) {
                                standalonePartnere.get().add(dbPerson);
                            }
                        })
                        .filter(relasjon ->
                                !Objects.equals(hovedperson.getIdent(), relasjon.getRelatertPerson()))
                        .collectList()
                        .doOnNext(relasjon -> dbPerson.getPerson().setForelderBarnRelasjon(relasjon))
                        .thenReturn(dbPerson))
                .flatMap(personRepository::save)
                .then()
                .thenReturn(standalonePartnere.get());
    }

    private Mono<Set<DbPerson>> deleteForeldreansvarRelasjoner(DbPerson hovedperson) {

        val standalonePartnere = new AtomicReference<>(new HashSet<DbPerson>());

        return relasjonRepository.findByPersonIdAndRelasjonTypeIn(hovedperson.getId(), List.of(FORELDREANSVAR_BARN, FORELDREANSVAR_FORELDER))
                .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                .doOnNext(dbPerson -> {
                    if (isTrue(dbPerson.getPerson().getStandalone())) {
                        standalonePartnere.get().add(dbPerson);
                    }
                })
                .flatMap(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getForeldreansvar())
                        .doOnNext(foreldreansvar -> {
                            if (foreldreansvar.isEksisterendePerson()) {
                                standalonePartnere.get().add(dbPerson);
                            }
                        })
                        .filter(foreldreansvar ->
                                !Objects.equals(hovedperson.getIdent(), foreldreansvar.getIdentForRelasjon()))
                        .collectList()
                        .doOnNext(foreldreansvar -> dbPerson.getPerson().setForeldreansvar(foreldreansvar))
                        .thenReturn(dbPerson))
                .flatMap(personRepository::save)
                .then()
                .thenReturn(standalonePartnere.get());
    }

    private Mono<Set<DbPerson>> deleteFullmaktRelasjoner(DbPerson hovedperson) {

        val standalonePartnere = new AtomicReference<>(new HashSet<DbPerson>());

        return relasjonRepository.findByPersonIdAndRelasjonTypeIn(hovedperson.getId(), List.of(FULLMAKTSGIVER, FULLMEKTIG))
                .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                .doOnNext(dbPerson -> {
                    if (isTrue(dbPerson.getPerson().getStandalone())) {
                        standalonePartnere.get().add(dbPerson);
                    }
                })
                .flatMap(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getFullmakt())
                        .doOnNext(fullmakt -> {
                            if (fullmakt.isEksisterendePerson()) {
                                standalonePartnere.get().add(dbPerson);
                            }
                        })
                        .filter(fullmakt ->
                                !Objects.equals(hovedperson.getIdent(), fullmakt.getMotpartsPersonident()))
                        .collectList()
                        .doOnNext(fullmakt -> dbPerson.getPerson().setFullmakt(fullmakt))
                        .thenReturn(dbPerson))
                .flatMap(personRepository::save)
                .then()
                .thenReturn(standalonePartnere.get());
    }

    private Mono<Set<DbPerson>> deleteVergemaalRelasjoner(DbPerson hovedperson) {

        val standalonePartnere = new AtomicReference<>(new HashSet<DbPerson>());

        return relasjonRepository.findByPersonIdAndRelasjonTypeIn(hovedperson.getId(), List.of(VERGE, VERGE_MOTTAKER))
                .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                .flatMap(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getVergemaal())
                        .doOnNext(vergemaal -> {
                            if (vergemaal.isEksisterendePerson()) {
                                standalonePartnere.get().add(dbPerson);
                            }
                        })
                        .filter(vergemaal ->
                                !Objects.equals(hovedperson.getIdent(), vergemaal.getIdentForRelasjon()))
                        .collectList()
                        .doOnNext(vergemaal -> dbPerson.getPerson().setVergemaal(vergemaal))
                        .thenReturn(dbPerson))
                .flatMap(personRepository::save)
                .then()
                .thenReturn(standalonePartnere.get());
    }

    private Mono<Set<DbPerson>> deleteKontaktinformasjonForDoedsboeAndreRelasjoner(DbPerson hovedperson) {

        val standalonePartnere = new AtomicReference<>(new HashSet<DbPerson>());

        return relasjonRepository.findByPersonIdAndRelasjonTypeIn(hovedperson.getId(), List.of(KONTAKT_FOR_DOEDSBO, AVDOEDD_FOR_KONTAKT))
                .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                .doOnNext(dbPerson -> {
                    if (isTrue(dbPerson.getPerson().getStandalone())) {
                        standalonePartnere.get().add(dbPerson);
                    }
                })
                .flatMap(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getKontaktinformasjonForDoedsbo())
                        .doOnNext(kontakt -> {
                            if (nonNull(kontakt.getPersonSomKontakt()) &&
                                kontakt.getPersonSomKontakt().isEksisterendePerson()) {
                                standalonePartnere.get().add(dbPerson);
                            }
                        })
                        .filter(kontakt ->
                                !Objects.equals(hovedperson.getIdent(), kontakt.getIdentForRelasjon()))
                        .collectList()
                        .doOnNext(kontakt -> dbPerson.getPerson().setKontaktinformasjonForDoedsbo(kontakt))
                        .thenReturn(dbPerson))
                .flatMap(personRepository::save)
                .then()
                .thenReturn(standalonePartnere.get());
    }

    private Mono<Void> deleteStandaloneRelasjoner(DbPerson hovedperson, Set<DbPerson> standalonePartnere) {

        return Mono.defer(() -> {

            if (hovedperson.getPerson().isStandalone() || !standalonePartnere.isEmpty()) {

                return Flux.fromIterable(standalonePartnere)
                        .flatMap(partner -> relasjonRepository.deleteByPersonIdAndRelatertPersonId(hovedperson.getId(), partner.getId())
                                .then(relasjonRepository.deleteByPersonIdAndRelatertPersonId(partner.getId(), hovedperson.getId())))
                        .then();
            }

            return Mono.empty();
        });
    }
}
