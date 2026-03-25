package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.utils.ArtifactUtils;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO.Ansvar;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.EKTEFELLE_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FALSK_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.KONTAKT_FOR_DOEDSBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtifactUpdateService {

    private static final String IDENT_NOT_FOUND = "Person med ident: %s ble ikke funnet";
    private static final String INFO_NOT_FOUND = "%s med id: %s ble ikke funnet";

    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
    private final DeleteRelasjonerService deleteRelasjonerService;
    private final DeltBostedService deltBostedService;
    private final DoedfoedtBarnService doedfoedtBarnService;
    private final DoedsfallService doedsfallService;
    private final FalskIdentitetService falskIdentitetService;
    private final FoedestedService foedestedService;
    private final FoedselService foedselService;
    private final FoedselsdatoService foedselsdatoService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;
    private final ForelderBarnRelasjonService forelderBarnRelasjonService;
    private final ForeldreansvarService foreldreansvarService;
    private final InnflyttingService innflyttingService;
    private final KjoennService kjoennService;
    private final KontaktAdresseService kontaktAdresseService;
    private final KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;
    private final NavnService navnService;
    private final OppholdService oppholdService;
    private final OppholdsadresseService oppholdsadresseService;
    private final PersonRepository personRepository;
    private final PersonService personService;
    private final RelasjonRepository relasjonRepository;
    private final SikkerhetstiltakService sikkerhetstiltakService;
    private final SivilstandService sivilstandService;
    private final StatsborgerskapService statsborgerskapService;
    private final TelefonnummerService telefonnummerService;
    private final TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;
    private final UtenlandsidentifikasjonsnummerService utenlandsidentifikasjonsnummerService;
    private final UtflyttingService utflyttingService;
    private final VergemaalService vergemaalService;

    public Mono<Void> updateFoedsel(String ident, Integer id, FoedselDTO oppdatertFoedsel) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getFoedsel(), oppdatertFoedsel, id, "Foedsel")
                        .doOnNext(foedsler -> dbPerson.getPerson().setFoedsel(foedsler))
                        .then(foedselService.validate(oppdatertFoedsel, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(foedselService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFoedested(String ident, Integer id, FoedestedDTO oppdatertFoedested) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getFoedested(), oppdatertFoedested, id, "Foedested")
                        .doOnNext(foedesteder -> dbPerson.getPerson().setFoedested(foedesteder))
                        .then(foedestedService.validate(oppdatertFoedested, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(foedestedService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFoedselsdato(String ident, Integer id, FoedselsdatoDTO oppdatertFoedselsdato) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getFoedselsdato(), oppdatertFoedselsdato, id, "Foedselsdato")
                        .doOnNext(foedselsdatoer -> dbPerson.getPerson().setFoedselsdato(foedselsdatoer))
                        .then(foedselsdatoService.validate(oppdatertFoedselsdato, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(foedselsdatoService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateNavn(String ident, Integer id, NavnDTO oppdatertNavn) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getNavn(), oppdatertNavn, id, "Navn")
                        .doOnNext(navn -> dbPerson.getPerson().setNavn(navn))
                        .then(navnService.validate(oppdatertNavn, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(navnService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateKjoenn(String ident, Integer id, KjoennDTO oppdatertKjoenn) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getKjoenn(), oppdatertKjoenn, id, "Kjoenn")
                        .doOnNext(kjoenn -> dbPerson.getPerson().setKjoenn(kjoenn))
                        .then(kjoennService.validate(oppdatertKjoenn, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(kjoennService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateBostedsadresse(String ident, Integer id, BostedadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getBostedsadresse(), oppdatertAdresse, id, "Bostedsadresse")
                        .doOnNext(bostedsadresser -> dbPerson.getPerson().setBostedsadresse(bostedsadresser))
                        .then(bostedAdresseService.validate(oppdatertAdresse, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(person -> bostedAdresseService.convert(person, false))
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateKontaktadresse(String ident, Integer id, KontaktadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getKontaktadresse(), oppdatertAdresse, id, "Kontaktadresse")
                        .doOnNext(kontaktadresser -> dbPerson.getPerson().setKontaktadresse(kontaktadresser))
                        .then(kontaktAdresseService.validate(oppdatertAdresse, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(person -> kontaktAdresseService.convert(person, false))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateOppholdsadresse(String ident, Integer id, OppholdsadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getOppholdsadresse(), oppdatertAdresse, id, "Oppholdsadresse")
                        .doOnNext(oppholdsadresser -> dbPerson.getPerson().setOppholdsadresse(oppholdsadresser))
                        .then(oppholdsadresseService.validate(oppdatertAdresse, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(oppholdsadresseService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateInnflytting(String ident, Integer id, InnflyttingDTO oppdatertInnflytting) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getInnflytting(), oppdatertInnflytting, id, "Innflytting")
                        .doOnNext(innflyttinger -> dbPerson.getPerson().setInnflytting(innflyttinger))
                        .then(innflyttingService.validate(oppdatertInnflytting).then(Mono.just(dbPerson))))
                .flatMap(innflyttingService::convert)
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateUtflytting(String ident, Integer id, UtflyttingDTO oppdatertUtflytting) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getUtflytting(), oppdatertUtflytting, id, "Utflytting")
                        .doOnNext(utflyttinger -> dbPerson.getPerson().setUtflytting(utflyttinger))
                        .then(utflyttingService.validate(oppdatertUtflytting).then(Mono.just(dbPerson))))
                .flatMap(utflyttingService::convert)
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDeltBosted(String ident, Integer id, DeltBostedDTO oppdatertDeltBosted) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getDeltBosted(), oppdatertDeltBosted, id, "DeltBosted")
                        .doOnNext(deltebosteder -> dbPerson.getPerson().setDeltBosted(deltebosteder))
                        .then(deltBostedService.prepAdresser(oppdatertDeltBosted).then(Mono.just(dbPerson))))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateForelderBarnRelasjon(String ident, Integer id, ForelderBarnRelasjonDTO oppdatertRelasjon) {

        return getPerson(ident)
                .flatMap(dbPerson -> forelderBarnRelasjonService.validate(oppdatertRelasjon, dbPerson.getPerson())
                        .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getForelderBarnRelasjon())
                        .filter(relasjon -> relasjon.getId().equals(id))
                        .filter(relasjon ->
                                isEndretRolle(relasjon, oppdatertRelasjon) ||
                                relasjon.isRelatertMedIdentifikator() &&
                                !Objects.equals(relasjon.getRelatertPerson(), oppdatertRelasjon.getRelatertPerson()))
                        .flatMap(relasjon -> getPerson(relasjon.getIdentForRelasjon())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(dbPerson, slettePerson, FAMILIERELASJON_BARN)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, relasjon.isEksisterendePerson()))
                                .then(updateArtifact(dbPerson.getPerson().getForelderBarnRelasjon(), oppdatertRelasjon, id, "ForelderBarnRelasjon"))
                                .doOnNext(forelderBarnRelasjoner -> {
                                    oppdatertRelasjon.setId(id);
                                    dbPerson.getPerson().getForelderBarnRelasjon().add(oppdatertRelasjon);
                                    dbPerson.getPerson().getForelderBarnRelasjon().sort(Comparator.comparing(ForelderBarnRelasjonDTO::getId).reversed());
                                    dbPerson.getPerson().setForelderBarnRelasjon(forelderBarnRelasjoner);
                                }))
                        .switchIfEmpty(updateArtifact(dbPerson.getPerson().getForelderBarnRelasjon(), oppdatertRelasjon, id, "ForelderBarnRelasjon")
                                .doOnNext(forelderBarnRelasjoner ->
                                        dbPerson.getPerson().getKontaktinformasjonForDoedsbo().sort(Comparator.comparing(KontaktinformasjonForDoedsboDTO::getId).reversed())))
                        .then(forelderBarnRelasjonService.convert(dbPerson))
                        .flatMap(this::savePerson))
                .then();
    }

    public Mono<Void> updateForeldreansvar(String ident, Integer id, ForeldreansvarDTO oppdatertAnsvar) {

        return getPerson(ident)
                .flatMap(dbPerson -> foreldreansvarService.validate(oppdatertAnsvar, dbPerson.getPerson())
                        .then(Mono.just(dbPerson)))
                .flatMapMany(person -> Flux.fromIterable(person.getPerson().getForeldreansvar())
                        .filter(relasjon -> relasjon.getId().equals(id))
                        .filter(ansvar -> oppdatertAnsvar.getAnsvar() != ansvar.getAnsvar() ||
                                          ansvar.isAnsvarligMedIdentifikator() &&
                                          !Objects.equals(ansvar.getAnsvarlig(), oppdatertAnsvar.getAnsvarlig()))
                        .flatMap(ansvar -> getPerson(ansvar.getAnsvarlig())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(person, slettePerson, FORELDREANSVAR_FORELDER)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, ansvar.isEksisterendePerson())
                                        .then(Mono.just(person)))
                                .flatMapMany(type -> Flux.fromIterable(person.getPerson().getForeldreansvar())
                                        .filter(ansvar1 -> ansvar1.getAnsvar() == Ansvar.FELLES)
                                        .filter(ForeldreansvarDTO::isAnsvarligMedIdentifikator)
                                        .filter(ansvar1 -> !ansvar1.getAnsvarlig().equals(ansvar.getAnsvarlig()))
                                        .flatMap(ansvar1 -> getPerson(ansvar1.getAnsvarlig())
                                                .flatMap(slettePerson ->
                                                        deleteRelasjonerService.deleteRelasjoner(person, slettePerson, FORELDREANSVAR_FORELDER)
                                                                .then(Mono.just(person)))))))
                .flatMap(person -> updateArtifact(person.getPerson().getForeldreansvar(), oppdatertAnsvar, id, "Foreldreansvar")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> {
                    oppdatertAnsvar.setId(id);
                    tuple.getT2().getPerson().getForeldreansvar().add(oppdatertAnsvar);
                    tuple.getT2().getPerson().getForeldreansvar().sort(Comparator.comparing(ForeldreansvarDTO::getId).reversed());
                    tuple.getT2().getPerson().setForeldreansvar(tuple.getT1());

                })
                .flatMap(tuple -> foreldreansvarService.handle(oppdatertAnsvar, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .doOnNext(person -> ArtifactUtils.renumberId(person.getPerson().getForeldreansvar()))
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateKontaktinformasjonForDoedsbo(String ident, Integer id, KontaktinformasjonForDoedsboDTO oppdatertInformasjon) {

        return kontaktinformasjonForDoedsboService.validate(oppdatertInformasjon)
                .then(getPerson(ident))
                .flatMapMany(person -> Flux.fromIterable(person.getPerson().getKontaktinformasjonForDoedsbo())
                        .filter(relasjon -> relasjon.getId().equals(id))
                        .filter(kontakt -> isNotBlank(kontakt.getIdentForRelasjon()) &&
                                           !Objects.equals(kontakt.getIdentForRelasjon(), oppdatertInformasjon.getIdentForRelasjon()))
                        .flatMap(kontakt -> getPerson(kontakt.getIdentForRelasjon())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(person, slettePerson, KONTAKT_FOR_DOEDSBO)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, kontakt.getPersonSomKontakt().isEksisterendePerson()))
                                .then(updateArtifact(person.getPerson().getKontaktinformasjonForDoedsbo(), oppdatertInformasjon, id, "KontaktinformasjonForDoedsbo"))
                                .doOnNext(kontaktinfo -> {
                                    oppdatertInformasjon.setId(id);
                                    person.getPerson().getKontaktinformasjonForDoedsbo().add(oppdatertInformasjon);
                                    person.getPerson().getKontaktinformasjonForDoedsbo().sort(Comparator.comparing(KontaktinformasjonForDoedsboDTO::getId).reversed());
                                    person.getPerson().setKontaktinformasjonForDoedsbo(kontaktinfo);
                                }))
                        .switchIfEmpty(updateArtifact(person.getPerson().getKontaktinformasjonForDoedsbo(), oppdatertInformasjon, id, "KontaktinformasjonForDoedsbo")
                                .doOnNext(type ->
                                        person.getPerson().getKontaktinformasjonForDoedsbo().sort(Comparator.comparing(KontaktinformasjonForDoedsboDTO::getId).reversed())))
                        .then(kontaktinformasjonForDoedsboService.convert(person)))
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateUtenlandskIdentifikasjonsnummer(String ident, Integer id, UtenlandskIdentifikasjonsnummerDTO oppdatertIdentifikasjon) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer(), oppdatertIdentifikasjon, id, "UtenlandskIdentifikasjonsnummer")
                        .doOnNext(utenlandskIdentifikasjon -> dbPerson.getPerson().setUtenlandskIdentifikasjonsnummer(utenlandskIdentifikasjon))
                        .then(utenlandsidentifikasjonsnummerService.validate(oppdatertIdentifikasjon).then(Mono.just(dbPerson))))
                .flatMap(utenlandsidentifikasjonsnummerService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFalskIdentitet(String ident, Integer id, FalskIdentitetDTO oppdatertIdentitet) {

        return falskIdentitetService.validate(oppdatertIdentitet)
                .then(getPerson(ident))
                .flatMapMany(person -> Flux.fromIterable(person.getPerson().getFalskIdentitet())
                        .filter(falskIdentitet -> falskIdentitet.getId().equals(id))
                        .filter(falskId -> isNotBlank(falskId.getRettIdentitetVedIdentifikasjonsnummer()) &&
                                           !Objects.equals(falskId.getRettIdentitetVedIdentifikasjonsnummer(),
                                                   oppdatertIdentitet.getRettIdentitetVedIdentifikasjonsnummer()))
                        .flatMap(falskId -> getPerson(falskId.getRettIdentitetVedIdentifikasjonsnummer())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(person, slettePerson, FALSK_IDENTITET)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, falskId.isEksisterendePerson())
                                        .then(Mono.just(person)))))
                .flatMap(person -> updateArtifact(person.getPerson().getFalskIdentitet(), oppdatertIdentitet, id, "FalskIdentitet")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> {
                    oppdatertIdentitet.setId(id);
                    tuple.getT2().getPerson().getFalskIdentitet().add(oppdatertIdentitet);
                    tuple.getT2().getPerson().getFalskIdentitet().sort(Comparator.comparing(FalskIdentitetDTO::getId).reversed());
                    tuple.getT2().getPerson().setFalskIdentitet(tuple.getT1());
                })
                .flatMap(tuple -> falskIdentitetService.convert(tuple.getT2()))
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateAdressebeskyttelse(String ident, Integer id, AdressebeskyttelseDTO oppdatertBeskyttelse) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getAdressebeskyttelse(), oppdatertBeskyttelse, id, "Adressebeskyttelse")
                        .doOnNext(adressebeskyttelser -> dbPerson.getPerson().setAdressebeskyttelse(adressebeskyttelser))
                        .then(adressebeskyttelseService.validate(oppdatertBeskyttelse, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(adressebeskyttelseService::convert)
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDoedsfall(String ident, Integer id, DoedsfallDTO oppdatertDoedsfall) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getDoedsfall(), oppdatertDoedsfall, id, "Doedsfall")
                        .doOnNext(doedsfall -> dbPerson.getPerson().setDoedsfall(doedsfall))
                        .then(doedsfallService.validate(oppdatertDoedsfall).then(Mono.just(dbPerson))))
                .flatMap(doedsfallService::convert)
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFolkeregisterPersonstatus(String ident, Integer id, FolkeregisterPersonstatusDTO oppdatertStatus) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getFolkeregisterPersonstatus(), oppdatertStatus, id, "FolkeregisterPersonstatus")
                        .doOnNext(personstatus -> dbPerson.getPerson().setFolkeregisterPersonstatus(personstatus))
                        .then(folkeregisterPersonstatusService.validate(oppdatertStatus, dbPerson.getPerson()).then(Mono.just(dbPerson))))
                .flatMap(folkeregisterPersonstatusService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateTilrettelagtKommunikasjon(String ident, Integer id, TilrettelagtKommunikasjonDTO oppdatertKommunikasjon) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getTilrettelagtKommunikasjon(), oppdatertKommunikasjon, id, "TilrettelagtKommunikasjon")
                        .doOnNext(tilrettelagtKommunikasjon -> dbPerson.getPerson().setTilrettelagtKommunikasjon(tilrettelagtKommunikasjon))
                        .then(tilrettelagtKommunikasjonService.validate(oppdatertKommunikasjon).then(Mono.just(dbPerson))))
                .flatMap(tilrettelagtKommunikasjonService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateStatsborgerskap(String ident, Integer id, StatsborgerskapDTO oppdatertStatsborgerskap) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getStatsborgerskap(), oppdatertStatsborgerskap, id, "Statsborgerskap")
                        .doOnNext(statsborgerskap -> dbPerson.getPerson().setStatsborgerskap(statsborgerskap))
                        .then(statsborgerskapService.validate(oppdatertStatsborgerskap).then(Mono.just(dbPerson))))
                .flatMap(statsborgerskapService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateOpphold(String ident, Integer id, OppholdDTO oppdatertOpphold) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getOpphold(), oppdatertOpphold, id, "Opphold")
                        .doOnNext(opphold -> dbPerson.getPerson().setOpphold(opphold))
                        .then(oppholdService.validate(oppdatertOpphold).then(Mono.just(dbPerson))))
                .flatMap(oppholdService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateSivilstand(String ident, Integer id, SivilstandDTO oppdatertSivilstand) {

        return getPerson(ident)
                .flatMap(dbPerson -> sivilstandService.validate(oppdatertSivilstand, dbPerson.getPerson())
                        .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getSivilstand())
                        .filter(sivilstand -> sivilstand.getId().equals(id))
                        .filter(sivilstand -> sivilstand.hasRelatertVedSivilstand() &&
                                              !Objects.equals(sivilstand.getRelatertVedSivilstand(), oppdatertSivilstand.getRelatertVedSivilstand()))
                        .flatMap(sivilstand -> getPerson(sivilstand.getRelatertVedSivilstand())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(dbPerson, slettePerson, EKTEFELLE_PARTNER)
                                                .then(deletePerson(slettePerson, sivilstand.isEksisterendePerson())))
                                .then(updateArtifact(dbPerson.getPerson().getSivilstand(), oppdatertSivilstand, id, "Sivilstand")
                                        .doOnNext(type -> {
                                            oppdatertSivilstand.setId(id);
                                            dbPerson.getPerson().getSivilstand().add(oppdatertSivilstand);
                                            dbPerson.getPerson().getSivilstand().sort(Comparator.comparing(SivilstandDTO::getId).reversed());
                                            dbPerson.getPerson().setSivilstand(type);
                                        })))
                        .switchIfEmpty(updateArtifact(dbPerson.getPerson().getSivilstand(), oppdatertSivilstand, id, "Sivilstand")
                                .doOnNext(type ->
                                        dbPerson.getPerson().getKontaktinformasjonForDoedsbo().sort(Comparator.comparing(KontaktinformasjonForDoedsboDTO::getId).reversed())))
                        .flatMap(tuple -> sivilstandService.convert(dbPerson))
                        .flatMap(this::savePerson))
                .then();
    }

    public Mono<Void> updateTelefonnummer(String ident, List<TelefonnummerDTO> oppdaterteTelefonnumre) {

        return getPerson(ident)
                .flatMap(person -> Flux.fromIterable(oppdaterteTelefonnumre)
                        .flatMap(telefonnummer -> telefonnummerService.validate(telefonnummer)
                                .then(Mono.just(telefonnummer)))
                        .doOnNext(telefonnummer -> {
                            telefonnummer.setIsNew(true);
                            telefonnummer.setId(telefonnummer.getPrioritet());
                        })
                        .collectList()
                        .doOnNext(telefonnumre ->
                                person.getPerson().setTelefonnummer(telefonnumre))
                        .flatMap(type -> telefonnummerService.convert(person)))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateVergemaal(String ident, Integer id, VergemaalDTO oppdatertVergemaal) {

        return vergemaalService.validate(oppdatertVergemaal)
                .then(getPerson(ident))
                .flatMapMany(person -> Flux.fromIterable(person.getPerson().getVergemaal())
                        .filter(vergemaal -> vergemaal.getId().equals(id))
                        .filter(vergemaal -> nonNull(vergemaal.getVergeIdent()) &&
                                             (isNotBlank(oppdatertVergemaal.getVergeIdent()) ||
                                              !Objects.equals(vergemaal.getVergeIdent(), oppdatertVergemaal.getVergeIdent())))
                        .flatMap(vergemaal -> getPerson(vergemaal.getVergeIdent())
                                .flatMap(slettePerson -> deleteRelasjonerService.deleteRelasjoner(person, slettePerson, VERGE)
                                        .then(Mono.just((slettePerson))))
                                .flatMap(slettePerson -> deletePerson(slettePerson, vergemaal.isEksisterendePerson())
                                        .then(Mono.just(vergemaal)))
                                .flatMapMany(type -> updateArtifact(person.getPerson().getVergemaal(), oppdatertVergemaal, id, "Vergemaal"))
                                .doOnNext(type -> {
                                    oppdatertVergemaal.setId(id);
                                    person.getPerson().getVergemaal().add(oppdatertVergemaal);
                                    person.getPerson().getVergemaal().sort(Comparator.comparing(VergemaalDTO::getId).reversed());
                                }))
                        .switchIfEmpty(updateArtifact(person.getPerson().getVergemaal(), oppdatertVergemaal, id, "Vergemaal")
                                .doOnNext(type ->
                                        person.getPerson().getVergemaal().sort(Comparator.comparing(VergemaalDTO::getId).reversed())))
                        .flatMap(vergemaal -> vergemaalService.convert(person)))
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateSikkerhetstiltak(String ident, Integer id, SikkerhetstiltakDTO oppdatertSikkerhetstiltak) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getSikkerhetstiltak(), oppdatertSikkerhetstiltak, id, "Sikkerhetstiltak")
                        .doOnNext(sikkerhetstiltak -> dbPerson.getPerson().setSikkerhetstiltak(sikkerhetstiltak))
                        .then(sikkerhetstiltakService.validate(oppdatertSikkerhetstiltak).then(Mono.just(dbPerson))))
                .flatMap(sikkerhetstiltakService::convert)
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDoedfoedtBarn(String ident, Integer id, DoedfoedtBarnDTO oppdatertDoedfoedt) {

        return getPerson(ident)
                .flatMap(dbPerson -> updateArtifact(dbPerson.getPerson().getDoedfoedtBarn(), oppdatertDoedfoedt, id, "DoedfoedtBarn")
                        .doOnNext(doedfoedtBarn -> dbPerson.getPerson().setDoedfoedtBarn(doedfoedtBarn))
                        .then(doedfoedtBarnService.validate(oppdatertDoedfoedt).then(Mono.just(dbPerson))))
                .flatMap(doedfoedtBarnService::convert)
                .flatMap(this::savePerson);
    }

    private <T extends DbVersjonDTO> Mono<List<T>> updateArtifact(List<T> artifacter, T artifact,
                                                                  Integer id, String navn) {

        artifact.setIsNew(true);
        artifact.setKilde(isNotBlank(artifact.getKilde()) ? artifact.getKilde() : "Dolly");
        artifact.setMaster(nonNull(artifact.getMaster()) ? artifact.getMaster() : DbVersjonDTO.Master.FREG);

        if (id.equals(0)) {
            artifacter.addFirst(initOpprett(artifacter, artifact));
            return Mono.just(artifacter);

        } else {
            return checkExists(artifacter, id, navn)
                    .then(Mono.defer(() -> Mono.just(new ArrayList<>(artifacter.stream()
                            .map(data -> {
                                if (data.getId().equals(id)) {
                                    artifact.setId(id);
                                    return artifact;
                                }
                                return data;
                            })
                            .toList()))));
        }
    }

    private Mono<DbPerson> getPerson(String ident) {

        return personRepository.findByIdent(ident.trim())
                .switchIfEmpty(Mono.error(new NotFoundException(String.format(IDENT_NOT_FOUND, ident))));
    }

    private Mono<Void> savePerson(DbPerson person) {

        return personRepository.save(person)
                .then();
    }

    private Mono<Void> deletePerson(DbPerson person, boolean isStandalonePerson) {

        return relasjonRepository.existsByPersonIdOrRelatertPersonId(person.getId())
                .flatMap(exists -> {
                    if (isFalse(exists) && !isStandalonePerson) {
                        return personService.deletePerson(person.getIdent());
                    }
                    return Mono.empty();
                });
    }

    private static <T extends DbVersjonDTO> Mono<Void> checkExists(List<T> artifacter, Integer id, String navn) {

        if (artifacter.stream().noneMatch(artifact -> artifact.getId().equals(id))) {
            return Mono.error(new NotFoundException(String.format(INFO_NOT_FOUND, navn, id)));
        }
        return Mono.empty();
    }

    private static <T extends DbVersjonDTO> T initOpprett(List<T> artifacter, T oppretting) {

        oppretting.setId(artifacter.stream()
                                 .mapToInt(DbVersjonDTO::getId)
                                 .max().orElse(0) + 1);
        return oppretting;
    }

    private static boolean isEndretRolle(ForelderBarnRelasjonDTO relasjon, ForelderBarnRelasjonDTO oppdatertRelasjon) {

        return oppdatertRelasjon.getMinRolleForPerson() != relasjon.getMinRolleForPerson() &&
               oppdatertRelasjon.getRelatertPersonsRolle() != relasjon.getRelatertPersonsRolle();
    }
}
