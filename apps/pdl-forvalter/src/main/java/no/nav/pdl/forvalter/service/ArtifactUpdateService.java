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
                .flatMap(person -> updateArtifact(person.getPerson().getFoedsel(), oppdatertFoedsel, id, "Foedsel")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setFoedsel(tuple.getT1()))
                .flatMap(tuple -> foedselService.validate(oppdatertFoedsel, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> foedselService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFoedested(String ident, Integer id, FoedestedDTO oppdatertFoedested) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getFoedested(), oppdatertFoedested, id, "Foedested")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setFoedested(tuple.getT1()))
                .flatMap(tuple -> foedestedService.validate(oppdatertFoedested, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> foedestedService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFoedselsdato(String ident, Integer id, FoedselsdatoDTO oppdatertFoedselsdato) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getFoedselsdato(), oppdatertFoedselsdato, id, "Foedselsdato")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setFoedselsdato(tuple.getT1()))
                .flatMap(tuple -> foedselsdatoService.validate(oppdatertFoedselsdato, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> foedselsdatoService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateNavn(String ident, Integer id, NavnDTO oppdatertNavn) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getNavn(), oppdatertNavn, id, "Navn")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setNavn(tuple.getT1()))
                .flatMap(tuple -> navnService.validate(oppdatertNavn, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> navnService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateKjoenn(String ident, Integer id, KjoennDTO oppdatertKjoenn) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getKjoenn(), oppdatertKjoenn, id, "Kjoenn")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setKjoenn(tuple.getT1()))
                .flatMap(tuple -> kjoennService.validate(oppdatertKjoenn, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> kjoennService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateBostedsadresse(String ident, Integer id, BostedadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getBostedsadresse(), oppdatertAdresse, id, "Bostedsadresse")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setBostedsadresse(tuple.getT1()))
                .flatMap(tuple -> bostedAdresseService.validate(oppdatertAdresse, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> bostedAdresseService.convert(person.getPerson(), false)
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateKontaktadresse(String ident, Integer id, KontaktadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getKontaktadresse(), oppdatertAdresse, id, "Kontaktadresse")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setKontaktadresse(tuple.getT1()))
                .flatMap(tuple -> kontaktAdresseService.validate(oppdatertAdresse, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> kontaktAdresseService.convert(person.getPerson(), false)
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateOppholdsadresse(String ident, Integer id, OppholdsadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getOppholdsadresse(), oppdatertAdresse, id, "Oppholdsadresse")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setOppholdsadresse(tuple.getT1()))
                .flatMap(tuple -> oppholdsadresseService.validate(oppdatertAdresse, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> oppholdsadresseService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateInnflytting(String ident, Integer id, InnflyttingDTO oppdatertInnflytting) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getInnflytting(), oppdatertInnflytting, id, "Innflytting")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setInnflytting(tuple.getT1()))
                .flatMap(tuple -> innflyttingService.validate(oppdatertInnflytting)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> innflyttingService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateUtflytting(String ident, Integer id, UtflyttingDTO oppdatertUtflytting) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getUtflytting(), oppdatertUtflytting, id, "Utflytting")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setUtflytting(tuple.getT1()))
                .flatMap(tuple -> utflyttingService.validate(oppdatertUtflytting)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> utflyttingService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDeltBosted(String ident, Integer id, DeltBostedDTO oppdatertDeltBosted) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getDeltBosted(), oppdatertDeltBosted, id, "DeltBosted")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setDeltBosted(tuple.getT1()))
                .flatMap(tuple -> deltBostedService.prepAdresser(oppdatertDeltBosted)
                        .thenReturn(tuple.getT2()))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateForelderBarnRelasjon(String ident, Integer id, ForelderBarnRelasjonDTO oppdatertRelasjon) {

        return getPerson(ident)
                .flatMap(person -> forelderBarnRelasjonService.validate(oppdatertRelasjon, person.getPerson())
                        .thenReturn(person))
                .flatMapMany(person -> Flux.fromIterable(person.getPerson().getForelderBarnRelasjon())
                        .filter(relasjon -> relasjon.getId().equals(id))
                        .filter(relasjon ->
                                isEndretRolle(relasjon, oppdatertRelasjon) ||
                                relasjon.isRelatertMedIdentifikator() &&
                                !Objects.equals(relasjon.getRelatertPerson(), oppdatertRelasjon.getRelatertPerson()))
                        .flatMap(relasjon -> getPerson(relasjon.getIdentForRelasjon())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(person, slettePerson, FAMILIERELASJON_BARN)
                                                .thenReturn(slettePerson))
                                .flatMap(slettePerson -> deletePerson(slettePerson, relasjon.isEksisterendePerson())
                                        .thenReturn(person))))
                .flatMap(person -> updateArtifact(person.getPerson().getForelderBarnRelasjon(), oppdatertRelasjon, id, "ForelderBarnRelasjon")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> {
                    oppdatertRelasjon.setId(id);
                    tuple.getT2().getPerson().getForelderBarnRelasjon().add(oppdatertRelasjon);
                    tuple.getT2().getPerson().getForelderBarnRelasjon().sort(Comparator.comparing(ForelderBarnRelasjonDTO::getId).reversed());
                    tuple.getT2().getPerson().setForelderBarnRelasjon(tuple.getT1());
                })
                .flatMap(tuple -> forelderBarnRelasjonService.convert(tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateForeldreansvar(String ident, Integer id, ForeldreansvarDTO oppdatertAnsvar) {

        return getPerson(ident)
                .flatMap(person -> foreldreansvarService.validate(oppdatertAnsvar, person.getPerson())
                        .thenReturn(person))
                .flatMapMany(person -> Flux.fromIterable(person.getPerson().getForeldreansvar())
                        .filter(relasjon -> relasjon.getId().equals(id))
                        .filter(ansvar -> oppdatertAnsvar.getAnsvar() != ansvar.getAnsvar() ||
                                          ansvar.isAnsvarligMedIdentifikator() &&
                                          !Objects.equals(ansvar.getAnsvarlig(), oppdatertAnsvar.getAnsvarlig()))
                        .flatMap(ansvar -> getPerson(ansvar.getAnsvarlig())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(person, slettePerson, FORELDREANSVAR_FORELDER)
                                                .thenReturn(slettePerson))
                                .flatMap(slettePerson -> deletePerson(slettePerson, ansvar.isEksisterendePerson())
                                        .thenReturn(person))
                                .flatMapMany(type -> Flux.fromIterable(person.getPerson().getForeldreansvar())
                                        .filter(ansvar1 -> ansvar1.getAnsvar() == Ansvar.FELLES)
                                        .filter(ForeldreansvarDTO::isAnsvarligMedIdentifikator)
                                        .filter(ansvar1 -> !ansvar1.getAnsvarlig().equals(ansvar.getAnsvarlig()))
                                        .flatMap(ansvar1 -> getPerson(ansvar1.getAnsvarlig())
                                                .flatMap(slettePerson ->
                                                        deleteRelasjonerService.deleteRelasjoner(person, slettePerson, FORELDREANSVAR_FORELDER)
                                                                .thenReturn(person))))))
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
                                                .thenReturn(slettePerson))
                                .flatMap(slettePerson -> deletePerson(slettePerson, kontakt.getPersonSomKontakt().isEksisterendePerson())
                                        .thenReturn(person))))
                .flatMap(person -> updateArtifact(person.getPerson().getKontaktinformasjonForDoedsbo(), oppdatertInformasjon, id, "KontaktinformasjonForDoedsbo")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> {
                    oppdatertInformasjon.setId(id);
                    tuple.getT2().getPerson().getKontaktinformasjonForDoedsbo().add(oppdatertInformasjon);
                    tuple.getT2().getPerson().getKontaktinformasjonForDoedsbo().sort(Comparator.comparing(KontaktinformasjonForDoedsboDTO::getId).reversed());
                    tuple.getT2().getPerson().setKontaktinformasjonForDoedsbo(tuple.getT1());
                })
                .flatMap(tuple -> kontaktinformasjonForDoedsboService.convert(tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateUtenlandskIdentifikasjonsnummer(String ident, Integer id, UtenlandskIdentifikasjonsnummerDTO oppdatertIdentifikasjon) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getUtenlandskIdentifikasjonsnummer(), oppdatertIdentifikasjon, id, "UtenlandskIdentifikasjonsnummer")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setUtenlandskIdentifikasjonsnummer(tuple.getT1()))
                .flatMap(tuple -> utenlandsidentifikasjonsnummerService.validate(oppdatertIdentifikasjon)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> utenlandsidentifikasjonsnummerService.convert(person.getPerson())
                        .thenReturn(person))
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
                                                .thenReturn(slettePerson))
                                .flatMap(slettePerson -> deletePerson(slettePerson, falskId.isEksisterendePerson())
                                        .thenReturn(person))))
                .flatMap(person -> updateArtifact(person.getPerson().getFalskIdentitet(), oppdatertIdentitet, id, "FalskIdentitet")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> {
                    oppdatertIdentitet.setId(id);
                    tuple.getT2().getPerson().getFalskIdentitet().add(oppdatertIdentitet);
                    tuple.getT2().getPerson().getFalskIdentitet().sort(Comparator.comparing(FalskIdentitetDTO::getId).reversed());
                    tuple.getT2().getPerson().setFalskIdentitet(tuple.getT1());
                })
                .flatMap(tuple -> falskIdentitetService.convert(tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateAdressebeskyttelse(String ident, Integer id, AdressebeskyttelseDTO oppdatertBeskyttelse) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getAdressebeskyttelse(), oppdatertBeskyttelse, id, "Adressebeskyttelse")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setAdressebeskyttelse(tuple.getT1()))
                .flatMap(tuple -> adressebeskyttelseService.validate(oppdatertBeskyttelse, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> adressebeskyttelseService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person ->
                        folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDoedsfall(String ident, Integer id, DoedsfallDTO oppdatertDoedsfall) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getDoedsfall(), oppdatertDoedsfall, id, "Doedsfall")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setDoedsfall(tuple.getT1()))
                .flatMap(tuple -> doedsfallService.validate(oppdatertDoedsfall)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> doedsfallService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFolkeregisterPersonstatus(String ident, Integer id, FolkeregisterPersonstatusDTO oppdatertStatus) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getFolkeregisterPersonstatus(), oppdatertStatus, id, "FolkeregisterPersonstatus")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setFolkeregisterPersonstatus(tuple.getT1()))
                .flatMap(tuple -> folkeregisterPersonstatusService.validate(oppdatertStatus, tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> folkeregisterPersonstatusService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateTilrettelagtKommunikasjon(String ident, Integer id, TilrettelagtKommunikasjonDTO oppdatertKommunikasjon) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getTilrettelagtKommunikasjon(), oppdatertKommunikasjon, id, "TilrettelagtKommunikasjon")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setTilrettelagtKommunikasjon(tuple.getT1()))
                .flatMap(tuple -> tilrettelagtKommunikasjonService.validate(oppdatertKommunikasjon)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> tilrettelagtKommunikasjonService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateStatsborgerskap(String ident, Integer id, StatsborgerskapDTO oppdatertStatsborgerskap) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getStatsborgerskap(), oppdatertStatsborgerskap, id, "Statsborgerskap")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setStatsborgerskap(tuple.getT1()))
                .flatMap(tuple -> statsborgerskapService.validate(oppdatertStatsborgerskap)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> statsborgerskapService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateOpphold(String ident, Integer id, OppholdDTO oppdatertOpphold) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getOpphold(), oppdatertOpphold, id, "Opphold")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setOpphold(tuple.getT1()))
                .flatMap(tuple -> oppholdService.validate(oppdatertOpphold)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> oppholdService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateSivilstand(String ident, Integer id, SivilstandDTO oppdatertSivilstand) {

        return getPerson(ident)
                .flatMap(person -> sivilstandService.validate(oppdatertSivilstand, person.getPerson())
                        .thenReturn(person))
                .flatMapMany(person -> Flux.fromIterable(person.getPerson().getSivilstand())
                        .filter(sivilstand -> sivilstand.getId().equals(id))
                        .filter(sivilstand -> sivilstand.hasRelatertVedSivilstand() &&
                                              !Objects.equals(sivilstand.getRelatertVedSivilstand(), oppdatertSivilstand.getRelatertVedSivilstand()))
                        .flatMap(sivilstand -> getPerson(sivilstand.getRelatertVedSivilstand())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(person, slettePerson, EKTEFELLE_PARTNER)
                                                .thenReturn(slettePerson))
                                .flatMap(slettePerson -> deletePerson(slettePerson, sivilstand.isEksisterendePerson())
                                        .thenReturn(person))))
                .flatMap(person -> updateArtifact(person.getPerson().getSivilstand(), oppdatertSivilstand, id, "Sivilstand")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> {
                    oppdatertSivilstand.setId(id);
                    tuple.getT2().getPerson().getSivilstand().add(oppdatertSivilstand);
                    tuple.getT2().getPerson().getSivilstand().sort(Comparator.comparing(SivilstandDTO::getId).reversed());
                    tuple.getT2().getPerson().setSivilstand(tuple.getT1());
                })
                .flatMap(tuple -> sivilstandService.convert(tuple.getT2().getPerson())
                        .thenReturn(tuple.getT2()))
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateTelefonnummer(String ident, List<TelefonnummerDTO> oppdaterteTelefonnumre) {

        return getPerson(ident)
                .flatMap(person -> Flux.fromIterable(oppdaterteTelefonnumre)
                        .flatMap(telefonnummer -> telefonnummerService.validate(telefonnummer)
                                .thenReturn(telefonnummer))
                        .doOnNext(telefonnummer -> {
                            telefonnummer.setIsNew(true);
                            telefonnummer.setId(telefonnummer.getPrioritet());
                        })
                        .collectList()
                        .doOnNext(telefonnumre ->
                                person.getPerson().setTelefonnummer(telefonnumre))
                        .flatMap(type -> telefonnummerService.convert(person.getPerson())
                                .thenReturn(person)))
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
                                        .thenReturn(slettePerson))
                                .flatMap(slettePerson -> deletePerson(slettePerson, vergemaal.isEksisterendePerson())
                                        .thenReturn(vergemaal))
                                .flatMap(type -> updateArtifact(person.getPerson().getVergemaal(), oppdatertVergemaal, id, "Vergemaal"))
                                .doOnNext(type -> {
                                    oppdatertVergemaal.setId(id);
                                    person.getPerson().getVergemaal().add(oppdatertVergemaal);
                                    person.getPerson().getVergemaal().sort(Comparator.comparing(VergemaalDTO::getId).reversed());
                                    person.getPerson().setVergemaal(type);
                                }))
                        .flatMap(vergemaal -> vergemaalService.convert(person.getPerson())
                                .thenReturn(person)))
                .flatMap(this::savePerson)
                .then();
    }

    public Mono<Void> updateSikkerhetstiltak(String ident, Integer id, SikkerhetstiltakDTO oppdatertSikkerhetstiltak) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getSikkerhetstiltak(), oppdatertSikkerhetstiltak, id, "Sikkerhetstiltak")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setSikkerhetstiltak(tuple.getT1()))
                .flatMap(tuple -> sikkerhetstiltakService.validate(oppdatertSikkerhetstiltak)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> sikkerhetstiltakService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDoedfoedtBarn(String ident, Integer id, DoedfoedtBarnDTO oppdatertDoedfoedt) {

        return getPerson(ident)
                .flatMap(person -> updateArtifact(person.getPerson().getDoedfoedtBarn(), oppdatertDoedfoedt, id, "DoedfoedtBarn")
                        .zipWith(Mono.just(person)))
                .doOnNext(tuple -> tuple.getT2().getPerson().setDoedfoedtBarn(tuple.getT1()))
                .flatMap(tuple -> doedfoedtBarnService.validate(oppdatertDoedfoedt)
                        .thenReturn(tuple.getT2()))
                .flatMap(person -> doedfoedtBarnService.convert(person.getPerson())
                        .thenReturn(person))
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
