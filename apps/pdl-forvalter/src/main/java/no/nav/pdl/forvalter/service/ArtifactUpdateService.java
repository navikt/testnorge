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
                .doOnNext(person ->
                        person.getPerson().setFoedsel(
                                updateArtifact(person.getPerson().getFoedsel(), oppdatertFoedsel, id, "Foedsel")))
                .flatMap(person -> foedselService.validate(oppdatertFoedsel, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> foedselService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFoedested(String ident, Integer id, FoedestedDTO oppdatertFoedested) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setFoedested(
                        updateArtifact(person.getPerson().getFoedested(), oppdatertFoedested, id, "Foedested")))
                .flatMap(person -> foedestedService.validate(oppdatertFoedested, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> foedestedService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFoedselsdato(String ident, Integer id, FoedselsdatoDTO oppdatertFoedselsdato) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setFoedselsdato(
                        updateArtifact(person.getPerson().getFoedselsdato(), oppdatertFoedselsdato, id, "Foedselsdato")))
                .flatMap(person -> foedselsdatoService.validate(oppdatertFoedselsdato, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> foedselsdatoService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateNavn(String ident, Integer id, NavnDTO oppdatertNavn) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setNavn(
                        updateArtifact(person.getPerson().getNavn(), oppdatertNavn, id, "Navn")))
                .flatMap(person -> navnService.validate(oppdatertNavn, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> navnService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateKjoenn(String ident, Integer id, KjoennDTO oppdatertKjoenn) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setKjoenn(
                        updateArtifact(person.getPerson().getKjoenn(), oppdatertKjoenn, id, "Kjoenn")))
                .flatMap(person -> kjoennService.validate(oppdatertKjoenn, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> kjoennService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateBostedsadresse(String ident, Integer id, BostedadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setBostedsadresse(
                        updateArtifact(person.getPerson().getBostedsadresse(), oppdatertAdresse, id, "Bostedsadresse")))
                .flatMap(person -> bostedAdresseService.validate(oppdatertAdresse, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> bostedAdresseService.convert(person.getPerson(), false)
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateKontaktadresse(String ident, Integer id, KontaktadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setKontaktadresse(
                        updateArtifact(person.getPerson().getKontaktadresse(), oppdatertAdresse, id, "Kontaktadresse")))
                .flatMap(person -> kontaktAdresseService.validate(oppdatertAdresse, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> kontaktAdresseService.convert(person.getPerson(), false)
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateOppholdsadresse(String ident, Integer id, OppholdsadresseDTO oppdatertAdresse) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setOppholdsadresse(
                        updateArtifact(person.getPerson().getOppholdsadresse(), oppdatertAdresse, id, "Oppholdsadresse")))
                .flatMap(person -> oppholdsadresseService.validate(oppdatertAdresse, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> oppholdsadresseService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateInnflytting(String ident, Integer id, InnflyttingDTO oppdatertInnflytting) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setInnflytting(
                        updateArtifact(person.getPerson().getInnflytting(), oppdatertInnflytting, id, "Innflytting")))
                .flatMap(person -> innflyttingService.validate(oppdatertInnflytting)
                        .thenReturn(person))
                .flatMap(person -> innflyttingService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateUtflytting(String ident, Integer id, UtflyttingDTO oppdatertUtflytting) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setUtflytting(
                        updateArtifact(person.getPerson().getUtflytting(), oppdatertUtflytting, id, "Utflytting")))
                .flatMap(person -> utflyttingService.validate(oppdatertUtflytting)
                        .thenReturn(person))
                .flatMap(person -> utflyttingService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDeltBosted(String ident, Integer id, DeltBostedDTO oppdatertDeltBosted) {

        return getPerson(ident)
                .doOnNext(person ->
                        person.getPerson().setDeltBosted(
                                updateArtifact(person.getPerson().getDeltBosted(), oppdatertDeltBosted, id, "DeltBosted")))
                .flatMap(person -> deltBostedService.prepAdresser(oppdatertDeltBosted)
                        .thenReturn(person))
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
                .doOnNext(person -> {
                    oppdatertRelasjon.setId(id);
                    person.getPerson().getForelderBarnRelasjon().add(oppdatertRelasjon);
                    person.getPerson().getForelderBarnRelasjon().sort(Comparator.comparing(ForelderBarnRelasjonDTO::getId).reversed());
                    person.getPerson().setForelderBarnRelasjon(
                            updateArtifact(person.getPerson().getForelderBarnRelasjon(), oppdatertRelasjon, id, "ForelderBarnRelasjon"));
                })
                .flatMap(person -> forelderBarnRelasjonService.convert(person.getPerson()))
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
                .doOnNext(person -> {
                    oppdatertAnsvar.setId(id);
                    person.getPerson().getForeldreansvar().add(oppdatertAnsvar);
                    person.getPerson().getForeldreansvar().sort(Comparator.comparing(ForeldreansvarDTO::getId).reversed());
                    person.getPerson().setForeldreansvar(
                            updateArtifact(person.getPerson().getForeldreansvar(), oppdatertAnsvar, id, "Foreldreansvar"));

                })
                .flatMap(person -> foreldreansvarService.handle(oppdatertAnsvar, person.getPerson())
                        .thenReturn(person))
                .doOnNext(person -> ArtifactUtils.renumberId(person.getPerson().getForeldreansvar()))
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
                .doOnNext(person -> {
                    oppdatertInformasjon.setId(id);
                    person.getPerson().getKontaktinformasjonForDoedsbo().add(oppdatertInformasjon);
                    person.getPerson().getKontaktinformasjonForDoedsbo().sort(Comparator.comparing(KontaktinformasjonForDoedsboDTO::getId).reversed());
                    person.getPerson().setKontaktinformasjonForDoedsbo(
                            updateArtifact(person.getPerson().getKontaktinformasjonForDoedsbo(), oppdatertInformasjon, id, "KontaktinformasjonForDoedsbo"));
                })
                .flatMap(person -> kontaktinformasjonForDoedsboService.convert(person.getPerson()))
                .then();
    }

    public Mono<Void> updateUtenlandskIdentifikasjonsnummer(String ident, Integer id, UtenlandskIdentifikasjonsnummerDTO oppdatertIdentifikasjon) {

        return getPerson(ident)
                .doOnNext(person ->
                        person.getPerson().setUtenlandskIdentifikasjonsnummer(
                                updateArtifact(person.getPerson().getUtenlandskIdentifikasjonsnummer(), oppdatertIdentifikasjon, id, "UtenlandskIdentifikasjonsnummer")))
                .flatMap(person -> utenlandsidentifikasjonsnummerService.validate(oppdatertIdentifikasjon)
                        .thenReturn(person))
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
                .doOnNext(person -> {
                    oppdatertIdentitet.setId(id);
                    person.getPerson().getFalskIdentitet().add(oppdatertIdentitet);
                    person.getPerson().getFalskIdentitet().sort(Comparator.comparing(FalskIdentitetDTO::getId).reversed());
                    person.getPerson().setFalskIdentitet(
                            updateArtifact(person.getPerson().getFalskIdentitet(), oppdatertIdentitet, id, "FalskIdentitet"));
                })
                .flatMap(person -> falskIdentitetService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson()))
                .then();
    }

    public Mono<Void> updateAdressebeskyttelse(String ident, Integer id, AdressebeskyttelseDTO oppdatertBeskyttelse) {

        return getPerson(ident)
                .doOnNext(person ->
                        person.getPerson().setAdressebeskyttelse(
                                updateArtifact(person.getPerson().getAdressebeskyttelse(), oppdatertBeskyttelse, id, "Adressebeskyttelse")))
                .flatMap(person -> adressebeskyttelseService.validate(oppdatertBeskyttelse, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> adressebeskyttelseService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDoedsfall(String ident, Integer id, DoedsfallDTO oppdatertDoedsfall) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setDoedsfall(
                        updateArtifact(person.getPerson().getDoedsfall(), oppdatertDoedsfall, id, "Doedsfall")))
                .flatMap(person -> doedsfallService.validate(oppdatertDoedsfall)
                        .thenReturn(person))
                .flatMap(person -> doedsfallService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.update(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateFolkeregisterPersonstatus(String ident, Integer id, FolkeregisterPersonstatusDTO oppdatertStatus) {

        return getPerson(ident)
                .doOnNext(person ->
                        person.getPerson().setFolkeregisterPersonstatus(
                                updateArtifact(person.getPerson().getFolkeregisterPersonstatus(), oppdatertStatus, id, "FolkeregisterPersonstatus")))
                .flatMap(person -> folkeregisterPersonstatusService.validate(oppdatertStatus, person.getPerson())
                        .thenReturn(person))
                .flatMap(person -> folkeregisterPersonstatusService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateTilrettelagtKommunikasjon(String ident, Integer id, TilrettelagtKommunikasjonDTO oppdatertKommunikasjon) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setTilrettelagtKommunikasjon(
                        updateArtifact(person.getPerson().getTilrettelagtKommunikasjon(), oppdatertKommunikasjon, id, "TilrettelagtKommunikasjon")))
                .flatMap(person -> tilrettelagtKommunikasjonService.validate(oppdatertKommunikasjon)
                        .thenReturn(person))
                .flatMap(person -> tilrettelagtKommunikasjonService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateStatsborgerskap(String ident, Integer id, StatsborgerskapDTO oppdatertStatsborgerskap) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setStatsborgerskap(
                        updateArtifact(person.getPerson().getStatsborgerskap(), oppdatertStatsborgerskap, id, "Statsborgerskap")))
                .flatMap(person -> statsborgerskapService.validate(oppdatertStatsborgerskap)
                        .thenReturn(person))
                .flatMap(person -> statsborgerskapService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateOpphold(String ident, Integer id, OppholdDTO oppdatertOpphold) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setOpphold(
                        updateArtifact(person.getPerson().getOpphold(), oppdatertOpphold, id, "Opphold")))
                .flatMap(person -> oppholdService.validate(oppdatertOpphold)
                        .thenReturn(person))
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
                .doOnNext(person -> {
                    oppdatertSivilstand.setId(id);
                    person.getPerson().getSivilstand().add(oppdatertSivilstand);
                    person.getPerson().getSivilstand().sort(Comparator.comparing(SivilstandDTO::getId).reversed());
                    person.getPerson().setSivilstand(
                            updateArtifact(person.getPerson().getSivilstand(), oppdatertSivilstand, id, "Sivilstand"));
                })
                .flatMap(person -> sivilstandService.convert(person.getPerson())
                        .thenReturn(person))
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
                        .then(telefonnummerService.convert(person.getPerson())));
    }

    public Mono<Void> updateVergemaal(String ident, Integer id, VergemaalDTO oppdatertVergemaal) {

        return vergemaalService.validate(oppdatertVergemaal)
                .then(getPerson(ident))
                .flatMapMany(person -> Flux.fromIterable(person.getPerson().getVergemaal())
                        .filter(vergemaal -> vergemaal.getId().equals(id))
                        .filter(vergemaal -> nonNull(vergemaal.getVergeIdent()) &&
                                             (isNotBlank(oppdatertVergemaal.getVergeIdent()) ||
                                              !Objects.equals(vergemaal.getVergeIdent(),oppdatertVergemaal.getVergeIdent())))
                        .flatMap(vergemaal -> getPerson(vergemaal.getVergeIdent())
                                .flatMap(slettePerson -> deleteRelasjonerService.deleteRelasjoner(person, slettePerson, VERGE)
                                        .thenReturn(slettePerson))
                                .flatMap(slettePerson -> deletePerson(slettePerson, vergemaal.isEksisterendePerson())
                                        .thenReturn(vergemaal))
                                .doOnNext(type -> {

                                    oppdatertVergemaal.setId(id);
                                    person.getPerson().getVergemaal().add(oppdatertVergemaal);
                                    person.getPerson().getVergemaal().sort(Comparator.comparing(VergemaalDTO::getId).reversed());
                                    person.getPerson().setVergemaal(
                                            updateArtifact(person.getPerson().getVergemaal(), oppdatertVergemaal, id, "Vergemaal"));
                                }))
                        .flatMap(vergemaal -> vergemaalService.convert(person.getPerson())
                                .thenReturn(vergemaal)))
                .then();
    }

    public Mono<Void> updateSikkerhetstiltak(String ident, Integer id, SikkerhetstiltakDTO oppdatertSikkerhetstiltak) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setSikkerhetstiltak(
                        updateArtifact(person.getPerson().getSikkerhetstiltak(), oppdatertSikkerhetstiltak, id, "Sikkerhetstiltak")))
                .flatMap(person -> sikkerhetstiltakService.validate(oppdatertSikkerhetstiltak)
                        .thenReturn(person))
                .flatMap(person -> sikkerhetstiltakService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    public Mono<Void> updateDoedfoedtBarn(String ident, Integer id, DoedfoedtBarnDTO oppdatertDoedfoedt) {

        return getPerson(ident)
                .doOnNext(person -> person.getPerson().setDoedfoedtBarn(
                        updateArtifact(person.getPerson().getDoedfoedtBarn(), oppdatertDoedfoedt, id, "DoedfoedtBarn")))
                .flatMap(person -> doedfoedtBarnService.validate(oppdatertDoedfoedt)
                        .thenReturn(person))
                .flatMap(person -> doedfoedtBarnService.convert(person.getPerson())
                        .thenReturn(person))
                .flatMap(this::savePerson);
    }

    private <T extends DbVersjonDTO> List<T> updateArtifact(List<T> artifacter, T artifact,
                                                            Integer id, String navn) {

        artifact.setIsNew(true);
        artifact.setKilde(isNotBlank(artifact.getKilde()) ? artifact.getKilde() : "Dolly");
        artifact.setMaster(nonNull(artifact.getMaster()) ? artifact.getMaster() : DbVersjonDTO.Master.FREG);

        if (id.equals(0)) {
            artifacter.addFirst(initOpprett(artifacter, artifact));
            return artifacter;

        } else {
            checkExists(artifacter, id, navn);
            return new ArrayList<>(artifacter.stream()
                    .map(data -> {
                        if (data.getId().equals(id)) {
                            artifact.setId(id);
                            return artifact;
                        }
                        return data;
                    })
                    .toList());
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

    private static <T extends DbVersjonDTO> void checkExists(List<T> artifacter, Integer id, String navn) {

        if (artifacter.stream().noneMatch(artifact -> artifact.getId().equals(id))) {
            throw new NotFoundException(String.format(INFO_NOT_FOUND, navn, id));
        }
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
