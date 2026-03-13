package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.nonNull;

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

        return Mono.just(hovedperson)
                .map(DbPerson::getRelasjoner)
                .flatMapMany(Flux::fromIterable)
                .filter(relasjon -> relasjon.getRelasjonType().equals(RelasjonType.EKTEFELLE_PARTNER))
                .map(DbRelasjon::getRelatertPerson)
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

        return Mono.just(hovedperson)
                .map(DbPerson::getRelasjoner)
                .flatMapMany(Flux::fromIterable)
                .filter(relasjon -> relasjon.getRelasjonType().equals(RelasjonType.FAMILIERELASJON_FORELDER) ||
                                    relasjon.getRelasjonType().equals(RelasjonType.FAMILIERELASJON_BARN))
                .map(DbRelasjon::getRelatertPerson)
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

        return Mono.just(hovedperson)
                .map(DbPerson::getRelasjoner)
                .flatMapMany(Flux::fromIterable)
                .filter(relasjon -> relasjon.getRelasjonType().equals(RelasjonType.FORELDREANSVAR_BARN) ||
                                    relasjon.getRelasjonType().equals(RelasjonType.FORELDREANSVAR_FORELDER))
                .map(DbRelasjon::getRelatertPerson)
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

        return Mono.just(hovedperson)
                .map(DbPerson::getRelasjoner)
                .flatMapMany(Flux::fromIterable)
                .filter(relasjon -> relasjon.getRelasjonType().equals(RelasjonType.FULLMAKTSGIVER) ||
                                    relasjon.getRelasjonType().equals(RelasjonType.FULLMEKTIG))
                .map(DbRelasjon::getRelatertPerson)
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

        return Mono.just(hovedperson)
                .map(DbPerson::getRelasjoner)
                .flatMapMany(Flux::fromIterable)
                .filter(relasjon -> relasjon.getRelasjonType().equals(RelasjonType.VERGE) ||
                                    relasjon.getRelasjonType().equals(RelasjonType.VERGE_MOTTAKER))
                .map(DbRelasjon::getRelatertPerson)
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

        return Mono.just(hovedperson)
                .map(DbPerson::getRelasjoner)
                .flatMapMany(Flux::fromIterable)
                .filter(relasjon -> relasjon.getRelasjonType().equals(RelasjonType.KONTAKT_FOR_DOEDSBO) ||
                                    relasjon.getRelasjonType().equals(RelasjonType.AVDOEDD_FOR_KONTAKT))
                .map(DbRelasjon::getRelatertPerson)
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
