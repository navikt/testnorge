package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.utils.DeleteRelasjonerUtility;
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.EKTEFELLE_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FALSK_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.KONTAKT_FOR_DOEDSBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtifactUpdateService {

    private static final String IDENT_NOT_FOUND = "Person med ident: %s ble ikke funnet";
    private static final String INFO_NOT_FOUND = "%s med id: %s ble ikke funnet";

    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
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

        return getPerson(ident);
        forelderBarnRelasjonService.validate(oppdatertRelasjon, person.getPerson());

        var foreldrebarnRelasjon = person.getPerson().getForelderBarnRelasjon().stream()
                .filter(relasjon -> relasjon.getId().equals(id))
                .findFirst();

        foreldrebarnRelasjon.ifPresent(relasjon -> {

            var endretRelasjon = isEndretRolle(relasjon, oppdatertRelasjon) ||
                                 relasjon.isRelatertMedIdentifikator() &&
                                 !relasjon.getRelatertPerson().equals(oppdatertRelasjon.getRelatertPerson());

            if (endretRelasjon && relasjon.isRelatertMedIdentifikator()) {

                var slettePerson = getPerson(relasjon.getIdentForRelasjon());
                DeleteRelasjonerUtility.deleteRelasjoner(person, slettePerson, FAMILIERELASJON_BARN);

                deletePerson(slettePerson, relasjon.isEksisterendePerson());

                oppdatertRelasjon.setId(id);
                person.getPerson().getForelderBarnRelasjon().add(oppdatertRelasjon);
                person.getPerson().getForelderBarnRelasjon().sort(Comparator.comparing(ForelderBarnRelasjonDTO::getId).reversed());
            }
        });

        person.getPerson().setForelderBarnRelasjon(
                updateArtifact(person.getPerson().getForelderBarnRelasjon(), oppdatertRelasjon, id, "ForelderBarnRelasjon"));

        if (id == 0 || foreldrebarnRelasjon.isPresent()) {

            forelderBarnRelasjonService.convert(person.getPerson());
        }
    }

    public Mono<Void> updateForeldreansvar(String ident, Integer id, ForeldreansvarDTO oppdatertAnsvar) {

        return getPerson(ident);
        foreldreansvarService.validate(oppdatertAnsvar, person.getPerson());

        var foreldreansvar = person.getPerson().getForeldreansvar().stream()
                .filter(relasjon -> relasjon.getId().equals(id))
                .findFirst();

        foreldreansvar.ifPresent(ansvar -> {
            var endretAnsvar = oppdatertAnsvar.getAnsvar() != ansvar.getAnsvar() ||
                               ansvar.isAnsvarligMedIdentifikator() &&
                               !ansvar.getAnsvarlig().equals(oppdatertAnsvar.getAnsvarlig());

            if (endretAnsvar && ansvar.isAnsvarligMedIdentifikator() &&
                !ansvar.getAnsvarlig().equals(oppdatertAnsvar.getAnsvarlig())) {

                var slettePerson = getPerson(ansvar.getAnsvarlig());
                DeleteRelasjonerUtility.deleteRelasjoner(person, slettePerson, FORELDREANSVAR_FORELDER);

                person.getPerson().getForeldreansvar().stream()
                        .filter(ansvar1 -> ansvar1.getAnsvar() == Ansvar.FELLES)
                        .filter(ForeldreansvarDTO::isAnsvarligMedIdentifikator)
                        .filter(ansvar1 -> !ansvar1.getAnsvarlig().equals(ansvar.getAnsvarlig()))
                        .findFirst()
                        .ifPresent(ansvar1 ->
                                DeleteRelasjonerUtility.deleteRelasjoner(person, getPerson(ansvar1.getAnsvarlig()), FORELDREANSVAR_FORELDER));

                deletePerson(slettePerson, ansvar.isEksisterendePerson());

                oppdatertAnsvar.setId(id);
                person.getPerson().getForeldreansvar().add(oppdatertAnsvar);
                person.getPerson().getForeldreansvar().sort(Comparator.comparing(ForeldreansvarDTO::getId).reversed());
            }
        });

        person.getPerson().setForeldreansvar(
                updateArtifact(person.getPerson().getForeldreansvar(), oppdatertAnsvar, id, "Foreldreansvar"));

        if (id == 0 || foreldreansvar.isPresent()) {

            foreldreansvarService.handle(oppdatertAnsvar, person.getPerson());
        }

        person.getPerson().getForeldreansvar().stream()
                .max(Comparator.comparing(ForeldreansvarDTO::getId))
                .ifPresent(max -> {
                    if (max.getId() > person.getPerson().getForeldreansvar().size()) {
                        person.getPerson().getForeldreansvar()
                                .forEach(ansvar -> ansvar.setId(ansvar.getId() - 1));
                    }
                });
    }

    public Mono<Void> updateKontaktinformasjonForDoedsbo(String ident, Integer id, KontaktinformasjonForDoedsboDTO oppdatertInformasjon) {

        kontaktinformasjonForDoedsboService.validate(oppdatertInformasjon);

        return getPerson(ident);
        var kontaktinformasjonRelasjon = person.getPerson().getKontaktinformasjonForDoedsbo().stream()
                .filter(relasjon -> relasjon.getId().equals(id))
                .findFirst();

        kontaktinformasjonRelasjon.ifPresent(kontakt -> {

            var endretRelasjon = nonNull(kontakt.getPersonSomKontakt()) &&
                                 (isNull(oppdatertInformasjon.getPersonSomKontakt()) ||
                                  !kontakt.getPersonSomKontakt().getIdentifikasjonsnummer().equals(
                                          oppdatertInformasjon.getPersonSomKontakt().getIdentifikasjonsnummer()));

            if (endretRelasjon) {

                var slettePerson = getPerson(kontakt.getPersonSomKontakt().getIdentifikasjonsnummer());
                DeleteRelasjonerUtility.deleteRelasjoner(person, slettePerson, KONTAKT_FOR_DOEDSBO);

                deletePerson(slettePerson, kontakt.getPersonSomKontakt().isEksisterendePerson());

                oppdatertInformasjon.setId(id);
                person.getPerson().getKontaktinformasjonForDoedsbo().add(oppdatertInformasjon);
                person.getPerson().getKontaktinformasjonForDoedsbo().sort(Comparator.comparing(KontaktinformasjonForDoedsboDTO::getId).reversed());
            }
        });

        person.getPerson().setKontaktinformasjonForDoedsbo(
                updateArtifact(person.getPerson().getKontaktinformasjonForDoedsbo(), oppdatertInformasjon, id, "KontaktinformasjonForDoedsbo"));

        if (id == 0 || kontaktinformasjonRelasjon.isPresent()) {

            kontaktinformasjonForDoedsboService.convert(person.getPerson());
        }
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

        falskIdentitetService.validate(oppdatertIdentitet);

        return getPerson(ident);
        var relatertFalskIdentitet = person.getPerson().getFalskIdentitet().stream()
                .filter(falskIdentitet -> falskIdentitet.getId().equals(id))
                .findFirst();

        relatertFalskIdentitet.ifPresent(falskId -> {

            var relasjonEndret = isNotBlank(falskId.getRettIdentitetVedIdentifikasjonsnummer()) &&
                                 falskId.getRettIdentitetVedIdentifikasjonsnummer().equals(
                                         oppdatertIdentitet.getRettIdentitetVedIdentifikasjonsnummer());

            if (relasjonEndret) {
                var slettePerson = getPerson(falskId.getRettIdentitetVedIdentifikasjonsnummer());
                DeleteRelasjonerUtility.deleteRelasjoner(person, slettePerson, FALSK_IDENTITET);

                deletePerson(slettePerson, falskId.isEksisterendePerson());

                oppdatertIdentitet.setId(id);
                person.getPerson().getFalskIdentitet().add(oppdatertIdentitet);
                person.getPerson().getFalskIdentitet().sort(Comparator.comparing(FalskIdentitetDTO::getId).reversed());
            }
        });

        person.getPerson().setFalskIdentitet(
                updateArtifact(person.getPerson().getFalskIdentitet(), oppdatertIdentitet, id, "FalskIdentitet"));

        if (id == 0 || relatertFalskIdentitet.isPresent()) {

            falskIdentitetService.convert(person.getPerson());
            folkeregisterPersonstatusService.update(person.getPerson());
        }
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

        return getPerson(ident);
        sivilstandService.validate(oppdatertSivilstand, person.getPerson());

        var sivilstandRelasjon = person.getPerson().getSivilstand().stream()
                .filter(sivilstand -> sivilstand.getId().equals(id))
                .findFirst();

        sivilstandRelasjon.ifPresent(eksisterendeSivilstand -> {

            var endretRelasjon = eksisterendeSivilstand.hasRelatertVedSivilstand() &&
                                 !eksisterendeSivilstand.getRelatertVedSivilstand().equals(oppdatertSivilstand.getRelatertVedSivilstand());

            if (endretRelasjon) {

                var slettePerson = getPerson(eksisterendeSivilstand.getRelatertVedSivilstand());
                DeleteRelasjonerUtility.deleteRelasjoner(person, slettePerson, EKTEFELLE_PARTNER);

                deletePerson(slettePerson, eksisterendeSivilstand.isEksisterendePerson());

                oppdatertSivilstand.setId(id);
                person.getPerson().getSivilstand().add(oppdatertSivilstand);
                person.getPerson().getSivilstand().sort(Comparator.comparing(SivilstandDTO::getId).reversed());
            }
        });

        person.getPerson().setSivilstand(
                updateArtifact(person.getPerson().getSivilstand(), oppdatertSivilstand, id, "Sivilstand"));

        if (id == 0 || sivilstandRelasjon.isPresent()) {

            sivilstandService.convert(person.getPerson());
        }
    }

    public Mono<Void> updateTelefonnummer(String ident, List<TelefonnummerDTO> oppdaterteTelefonnumre) {

        return getPerson(ident);

        oppdaterteTelefonnumre
                .forEach(telefonnummer -> {
                    telefonnummerService.validate(telefonnummer);
                    telefonnummer.setIsNew(true);
                    telefonnummer.setId(telefonnummer.getPrioritet());
                });

        person.getPerson().setTelefonnummer(oppdaterteTelefonnumre);
        telefonnummerService.convert(person.getPerson().getTelefonnummer());
    }

    public Mono<Void> updateVergemaal(String ident, Integer id, VergemaalDTO oppdatertVergemaal) {

        return vergemaalService.validate(oppdatertVergemaal)
                .then(getPerson(ident))
                .flatMap(person -> Flux.fromIterable(person.getPerson().getVergemaal())
                        .filter(vergemaal -> vergemaal.getId().equals(id))
                        .next()
                        .flatMap(vergemaal -> {

                            val endretRelasjon = nonNull(vergemaal.getVergeIdent()) &&
                                                 (isNotBlank(oppdatertVergemaal.getVergeIdent()) ||
                                                  !vergemaal.getVergeIdent().equals(oppdatertVergemaal.getVergeIdent()));

                            if (endretRelasjon) {

                                return getPerson(vergemaal.getVergeIdent())
                                        .flatMap(slettePerson -> DeleteRelasjonerUtility.deleteRelasjoner(person, slettePerson, VERGE)
                                                .thenReturn(slettePerson))
                                        .flatMap(slettePerson -> deletePerson(slettePerson, vergemaal.isEksisterendePerson())
                                                .thenReturn(vergemaal))
                                                .doOnNext(type -> {

                                oppdatertVergemaal.setId(id);
                                person.getPerson().getVergemaal().add(oppdatertVergemaal);
                                person.getPerson().getVergemaal().sort(Comparator.comparing(VergemaalDTO::getId).reversed());
                            });
                            }
                            return Mono.just(vergemaal);
                        })
                        .flatMap(vergemaalRelasjon -> {
                            person.getPerson().setVergemaal(
                                    updateArtifact(person.getPerson().getVergemaal(), oppdatertVergemaal, id, "Vergemaal"));

                            if (id == 0) {
                                return vergemaalService.convert(person.getPerson());
                            }
                            return Mono.empty();
                        }));
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

    private Mono<Void> deletePerson(DbPerson person, boolean isEksisterendePerson) {

        if (person.getRelasjoner().isEmpty() && !isEksisterendePerson) {

            return personService.deletePerson(person.getIdent());
        }
        return Mono.empty();
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
