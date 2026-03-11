package no.nav.pdl.forvalter.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.pdl.forvalter.utils.KjoennUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO.Ansvar;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonnavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelatertBiPersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FAR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MEDMOR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MOR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ForeldreansvarService implements BiValidation<ForeldreansvarDTO, PersonDTO> {

    private static final int MYNDIG_ALDER = 18;
    private static final String INVALID_EMPTY_ANSVAR_EXCEPTION = "Forelderansvar: hvem som har ansvar må oppgis";
    private static final String INVALID_AMBIGUOUS_ANSVARLIG_EXCEPTION = "Forelderansvar: kun et av feltene 'ansvarlig' " +
                                                                        "og 'ansvarligUtenIdentifikator' kan benyttes";
    private static final String INVALID_ANSVARLIG_PERSON_EXCEPTION = "Foreldreansvar: Ansvarlig person %s finnes ikke";
    private static final String INVALID_NAVN_ERROR = "Foreldreansvar: Navn er ikke i liste over gyldige verdier";
    private static final String BARN_MANGLER = "Foreldreansvar: barn mangler / ";
    private static final String INVALID_RELASJON_MOR_EXCEPTION = BARN_MANGLER +
                                                                 "barnets foreldrerelasjon til mor ikke funnet";
    private static final String INVALID_RELASJON_FAR_EXCEPTION = BARN_MANGLER +
                                                                 "barnets foreldrerelasjon til far ikke funnet";
    private static final String INVALID_RELASJON_FELLES_EXCEPTION = BARN_MANGLER +
                                                                    "barnets foreldrerelasjon til mor og/eller far ikke funnet";

    private static final Random RANDOM = new SecureRandom();

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final MapperFacade mapperFacade;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    public Mono<Void> convert(PersonDTO person) {

        var alleForeldreansvar = mapperFacade.mapAsList(person.getForeldreansvar(), ForeldreansvarDTO.class);

        return Flux.fromIterable(alleForeldreansvar)
                .flatMap(type -> Flux.fromIterable(person.getForeldreansvar())
                        .filter(ansvar -> Objects.equals(type.getId(), ansvar.getId()))
                        .flatMap(foreldreansvar -> {
                            if (isTrue(foreldreansvar.getIsNew())) {

                                foreldreansvar.setKilde(getKilde(foreldreansvar));
                                foreldreansvar.setMaster(getMaster(foreldreansvar, person));
                                return handle(foreldreansvar, person);
                            }
                            return Mono.empty();
                        }))
                .then();
    }

    @Override
    public Mono<Void> validate(ForeldreansvarDTO foreldreansvar, PersonDTO hovedperson) {

        return validateForeldreansvar(foreldreansvar)
                .then(Mono.defer(() -> {

                    if (getFoedselsdato(hovedperson).stream()
                            .anyMatch(alder -> alder.getFoedselsaar() <= LocalDateTime.now().minusYears(MYNDIG_ALDER).getYear())) {

                        if ((foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) &&
                            isBlank(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                            !isRelasjonMor(hovedperson)) {
                            return Mono.error(new InvalidRequestException(INVALID_RELASJON_MOR_EXCEPTION));
                        }

                        if ((foreldreansvar.getAnsvar() == Ansvar.FAR) &&
                            isBlank(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                            !isRelasjonFar(hovedperson)) {
                            return Mono.error(new InvalidRequestException(INVALID_RELASJON_FAR_EXCEPTION));
                        }

                        if ((foreldreansvar.getAnsvar() == Ansvar.FELLES) &&
                            isBlank(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                            !isRelasjonForeldre(hovedperson)) {
                            return Mono.error(new InvalidRequestException(INVALID_RELASJON_FELLES_EXCEPTION));
                        }
                    } else {

                        validateBarn(foreldreansvar, hovedperson);
                    }
                    return Mono.empty();
                }));
    }

    private List<? extends FoedselsdatoDTO> getFoedselsdato(PersonDTO person) {

        if (!person.getFoedselsdato().isEmpty() && nonNull(person.getFoedselsdato().getFirst().getFoedselsaar())) {
            return person.getFoedselsdato();
        } else if (!person.getFoedsel().isEmpty() && nonNull(person.getFoedsel().getFirst().getFoedselsaar())) {
            return person.getFoedsel();
        } else {
            return List.of(FoedselsdatoDTO.builder()
                    .foedselsaar(DatoFraIdentUtility.getDato(person.getIdent()).getYear())
                    .build());
        }
    }

    private Mono<Void> validateForeldreansvar(ForeldreansvarDTO foreldreansvar) {

        if (isNull(foreldreansvar.getAnsvar())) {
            return Mono.error(new InvalidRequestException(INVALID_EMPTY_ANSVAR_EXCEPTION));
        }

        if (nonNull(foreldreansvar.getAnsvarlig()) &&
            nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

            return Mono.error(new InvalidRequestException(INVALID_AMBIGUOUS_ANSVARLIG_EXCEPTION));
        }

        return Mono.defer(() -> {
                    if (isNotBlank(foreldreansvar.getAnsvarlig())) {
                        return personRepository.existsByIdent(foreldreansvar.getAnsvarlig())
                                .flatMap(exists -> isFalse(exists) ?
                                        Mono.error(new InvalidRequestException(
                                                INVALID_ANSVARLIG_PERSON_EXCEPTION.formatted(foreldreansvar.getAnsvarlig()))) :
                                        Mono.empty());
                    }
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    if (nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

                        var navn = foreldreansvar.getAnsvarligUtenIdentifikator().getNavn();
                        if (nonNull(navn) && (isNotBlank(navn.getFornavn()) ||
                                              isNotBlank(navn.getMellomnavn()) ||
                                              isNotBlank(navn.getEtternavn()))) {

                            return genererNavnServiceConsumer.verifyNavn(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                                            .adjektiv(navn.getFornavn())
                                            .adverb(navn.getMellomnavn())
                                            .substantiv(navn.getEtternavn())
                                            .build())
                                    .flatMap(valid -> isFalse(valid) ?
                                            Mono.error(new InvalidRequestException(INVALID_NAVN_ERROR)) :
                                            Mono.empty());
                        }
                    }
                    return Mono.empty();
                }));
    }

    private Mono<Void> validateBarn(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {

        if ((foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) &&
            isBlank(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
            !isRelasjonFraBarn(barn, MOR, MEDMOR)) {
            return Mono.error(new InvalidRequestException(INVALID_RELASJON_MOR_EXCEPTION));
        }

        if ((foreldreansvar.getAnsvar() == Ansvar.FAR) &&
            isBlank(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
            !isRelasjonFraBarn(barn, FAR)) {
            return Mono.error(new InvalidRequestException(INVALID_RELASJON_FAR_EXCEPTION));
        }

        if ((foreldreansvar.getAnsvar() == Ansvar.FELLES) &&
            isBlank(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
            !isRelasjonForeldreFraBarn(barn)) {
            return Mono.error(new InvalidRequestException(INVALID_RELASJON_FELLES_EXCEPTION));
        }
        return Mono.empty();
    }

    private static boolean isRelasjonForeldreFraBarn(PersonDTO barn) {

        return barn.getForelderBarnRelasjon().stream()
                       .filter(ForelderBarnRelasjonDTO::isBarn)
                       .count() == 2;
    }

    private static boolean isRelasjonFraBarn(PersonDTO barn, Rolle... roller) {

        return barn.getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isBarn)
                .anyMatch(relasjon -> List.of(roller).contains(relasjon.getRelatertPersonsRolle()));
    }

    private boolean isRelasjonForeldre(PersonDTO person) {

        return person.getForelderBarnRelasjon().stream()
                       .anyMatch(relasjon -> relasjon.isForeldre() && isNotTrue(relasjon.getPartnerErIkkeForelder())) &&
               person.getSivilstand().stream()
                       .anyMatch(sivilstand -> (sivilstand.isGift() || sivilstand.isSeparert()));
    }

    private boolean isRelasjonMor(PersonDTO person) {

        return person.getForelderBarnRelasjon().stream()
                .anyMatch(relasjon ->
                        MOR == relasjon.getMinRolleForPerson() ||
                        MEDMOR == relasjon.getMinRolleForPerson() ||

                        (FORELDER == relasjon.getMinRolleForPerson() &&
                         (Kjoenn.KVINNE == person.getKjoenn().stream().findFirst().orElse(new KjoennDTO()).getKjoenn() ||
                          Kjoenn.KVINNE == KjoennFraIdentUtility.getKjoenn(person.getIdent()))) ||

                        isNotTrue(relasjon.getPartnerErIkkeForelder()) &&
                        person.getSivilstand().stream()
                                .anyMatch(sivilstand -> (sivilstand.isGift() || sivilstand.isSeparert())));
    }

    private Flux<BarnRelasjon> getBarnMorRelasjoner(PersonDTO person) {

        return Flux.fromIterable(person.getForelderBarnRelasjon())
                .flatMap(barnRelasjon -> personRepository.findByIdent(barnRelasjon.getRelatertPerson())
                        .flatMapMany(barn -> Flux.fromIterable(barn.getPerson().getForelderBarnRelasjon())
                                .filter(foreldreRelasjon -> foreldreRelasjon.getRelatertPersonsRolle() == Rolle.MOR ||
                                                            foreldreRelasjon.getRelatertPersonsRolle() == Rolle.MEDMOR)
                                .map(foreldreRelasjon -> BarnRelasjon.builder()
                                        .barn(barn)
                                        .ansvarlig(foreldreRelasjon.getRelatertPerson())
                                        .build())));
    }

    private boolean isRelasjonFar(PersonDTO person) {

        return person.getForelderBarnRelasjon().stream()
                .anyMatch(relasjon -> FAR == relasjon.getMinRolleForPerson() ||

                                      (FORELDER == relasjon.getMinRolleForPerson() &&
                                       (Kjoenn.MANN == person.getKjoenn().stream().findFirst().orElse(new KjoennDTO()).getKjoenn() ||
                                        Kjoenn.MANN == KjoennFraIdentUtility.getKjoenn(person.getIdent()))) ||

                                      isNotTrue(relasjon.getPartnerErIkkeForelder()) &&
                                      person.getSivilstand().stream()
                                              .anyMatch(sivilstand -> sivilstand.isGift() || sivilstand.isSeparert()));
    }

    private Flux<BarnRelasjon> getBarnFarRelasjoner(PersonDTO person) {

        return Flux.fromIterable(person.getForelderBarnRelasjon())
                .flatMap(barnRelasjon -> personRepository.findByIdent(barnRelasjon.getRelatertPerson())
                        .flatMapMany(barn -> Flux.fromIterable(barn.getPerson().getForelderBarnRelasjon())
                                .filter(foreldreRelasjon -> foreldreRelasjon.getRelatertPersonsRolle() == Rolle.FAR)
                                .map(foreldreRelasjon -> BarnRelasjon.builder()
                                        .barn(barn)
                                        .ansvarlig(foreldreRelasjon.getRelatertPerson())
                                        .build())));
    }

    public Mono<Void> handle(ForeldreansvarDTO foreldreansvar, PersonDTO person) {

        if (getFoedselsdato(person).stream()
                .anyMatch(alder -> alder.getFoedselsaar() <= LocalDateTime.now().minusYears(MYNDIG_ALDER).getYear())) {
            // hovedperson er voksen

            return Mono.defer(() -> {
                        if (isNotBlank(foreldreansvar.getAnsvarssubjekt())) {

                            return personRepository.findByIdent(foreldreansvar.getAnsvarssubjekt())
                                    .doOnNext(barn ->
                                            barn.getPerson().getForeldreansvar()
                                                    .addFirst(ForeldreansvarDTO.builder()
                                                            .eksisterendePerson(true)
                                                            .ansvar(foreldreansvar.getAnsvar())
                                                            .ansvarlig(person.getIdent())
                                                            .gyldigFraOgMed(foreldreansvar.getGyldigFraOgMed())
                                                            .gyldigFraOgMed(foreldreansvar.getGyldigTilOgMed())
                                                            .kilde(foreldreansvar.getKilde())
                                                            .master(foreldreansvar.getMaster())
                                                            .id(barn.getPerson().getForeldreansvar().stream()
                                                                        .map(ForeldreansvarDTO::getId)
                                                                        .findFirst().orElse(0) + 1)
                                                            .build()))
                                    .flatMap(barn ->
                                            relasjonService.setRelasjoner(barn.getIdent(), FORELDREANSVAR_FORELDER, person.getIdent(), FORELDREANSVAR_BARN));
                        }
                        return Mono.empty();
                    })
                    .then(Mono.defer(() -> {
                        if (foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) {

                            return getBarnMorRelasjoner(person)
                                    .flatMap(barnMorRelasjon ->
                                            setRelasjon(barnMorRelasjon, foreldreansvar, person.getIdent())
                                                    .thenReturn(barnMorRelasjon))
                                    .doOnNext(barnMorRelasjon ->
                                            slettRelasjonForHovedperson(person, foreldreansvar.getId(), barnMorRelasjon))
                                    .then();

                        } else if (foreldreansvar.getAnsvar() == Ansvar.FAR) {

                            return getBarnFarRelasjoner(person)
                                    .flatMap(barnFarRelasjon ->
                                            setRelasjon(barnFarRelasjon, foreldreansvar, person.getIdent())
                                                    .thenReturn(barnFarRelasjon))
                                    .doOnNext(barnFarRelasjon ->
                                            slettRelasjonForHovedperson(person, foreldreansvar.getId(), barnFarRelasjon))
                                    .then();

                        } else if (foreldreansvar.getAnsvar() == Ansvar.FELLES) {

                            return Flux.concat(getBarnMorRelasjoner(person),
                                            getBarnFarRelasjoner(person))
                                    .flatMap(barnRelasjon ->
                                            setRelasjon(barnRelasjon, foreldreansvar, person.getIdent()))
                                    .then();

                        } else if (foreldreansvar.getAnsvar() == Ansvar.ANDRE) {

                            return Mono.defer(() -> {
                                        if (nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

                                            return makeAnsvarligUtenIdentifikator(foreldreansvar, person);

                                        } else if (isBlank(foreldreansvar.getAnsvarlig())) {

                                            return opprettNyAsvarlig(foreldreansvar, person);
                                        }
                                        return Mono.empty();
                                    })
                                    .then(Mono.defer(() -> {

                                        foreldreansvar.setEksisterendePerson(isNotBlank(foreldreansvar.getAnsvarlig()));

                                        return getBarnRelasjoner(foreldreansvar, person)
                                                .flatMap(barnRelasjon -> setRelasjon(barnRelasjon, foreldreansvar, person.getIdent())
                                                        .thenReturn(barnRelasjon))
                                                .doOnNext(barnRelasjon -> slettForeldreansvar(person, foreldreansvar.getId()))
                                                .then();
                                    }));

                        } else if (foreldreansvar.getAnsvar() == Ansvar.UKJENT) {

                            return makeAnsvarligUtenIdentifikator(foreldreansvar, person)
                                    .then(getBarnRelasjoner(foreldreansvar, person)
                                            .flatMap(barnRelasjon -> setRelasjon(barnRelasjon, foreldreansvar, person.getIdent())
                                                    .thenReturn(barnRelasjon))
                                            .doOnNext(barnRelasjon -> slettForeldreansvar(person, foreldreansvar.getId()))
                                            .then());
                        }
                        return Mono.empty();
                    }));

        } else {
            // hovedperson er barn
            return handleBarn(foreldreansvar, person);
        }
    }

    private static void slettRelasjonForHovedperson(PersonDTO person, Integer id, BarnRelasjon barnForelderRelasjon) {

        if (!Objects.equals(barnForelderRelasjon.getAnsvarlig(), person.getIdent())) {

            slettForeldreansvar(person, id);
        }
    }

    private static void slettForeldreansvar(PersonDTO person, Integer id) {

        person.setForeldreansvar(person.getForeldreansvar().stream()
                .filter(ansvar -> !Objects.equals(id, ansvar.getId()))
                .toList());
    }

    private Flux<BarnRelasjon> getBarnRelasjoner(ForeldreansvarDTO foreldreansvar, PersonDTO person) {

        return Flux.fromIterable(person.getForelderBarnRelasjon())
                .filter(ForelderBarnRelasjonDTO::hasBarn)
                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                .flatMap(personRepository::findByIdent)
                .map(dbperson -> BarnRelasjon.builder()
                        .ansvarlig(foreldreansvar.getAnsvarlig())
                        .barn(dbperson)
                        .build());
    }

    private Mono<Void> handleBarn(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {

        return Mono.defer(() -> {
                    if (isNotBlank(foreldreansvar.getAnsvarlig())) {

                        foreldreansvar.setEksisterendePerson(isTrue(foreldreansvar.getEksisterendePerson()));
                        return relasjonService.setRelasjoner(barn.getIdent(), FORELDREANSVAR_FORELDER, foreldreansvar.getAnsvarlig(), FORELDREANSVAR_BARN)
                                .then(oppdaterRelatertAnsvar(foreldreansvar, barn, foreldreansvar.getAnsvarlig()));

                    } else if (foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) {

                        return setRelasjoner(foreldreansvar, barn, MOR, MEDMOR);

                    } else if (foreldreansvar.getAnsvar() == Ansvar.FAR) {

                        return setRelasjoner(foreldreansvar, barn, FAR);

                    } else if (foreldreansvar.getAnsvar() == Ansvar.FELLES) {

                        return setFellesRelasjoner(foreldreansvar, barn);

                    } else if (foreldreansvar.getAnsvar() == Ansvar.ANDRE) {

                        return Mono.defer(() -> {
                                    if (nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

                                        return setAnsvarUtenIdentifikator(foreldreansvar, barn);

                                    } else if (isBlank(foreldreansvar.getAnsvarlig())) {

                                        return opprettNyAsvarlig(foreldreansvar, barn);
                                    }
                                    return Mono.empty();
                                })
                                .then(Mono.defer(() -> {

                                    if (isNotBlank(foreldreansvar.getAnsvarlig())) {
                                        foreldreansvar.setEksisterendePerson(true);
                                        return relasjonService.setRelasjoner(barn.getIdent(), FORELDREANSVAR_FORELDER, foreldreansvar.getAnsvarlig(), FORELDREANSVAR_BARN)
                                                .then(oppdaterRelatertAnsvar(foreldreansvar, barn, foreldreansvar.getAnsvarlig()));
                                    }
                                    return Mono.empty();
                                }));

                    } else if (foreldreansvar.getAnsvar() == Ansvar.UKJENT && nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

                        return setAnsvarUtenIdentifikator(foreldreansvar, barn);
                    }
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    foreldreansvar.setNyAnsvarlig(null);
                    return Mono.empty();
                }));
    }

    private Mono<Void> setAnsvarUtenIdentifikator(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {

        return Flux.fromIterable(barn.getForelderBarnRelasjon())
                .filter(ForelderBarnRelasjonDTO::isBarn)
                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                .next()
                .flatMap(personRepository::findByIdent)
                .flatMap(dbPerson -> makeAnsvarligUtenIdentifikator(foreldreansvar, dbPerson.getPerson()))
                .then();
    }

    private Mono<Void> opprettNyAsvarlig(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {

        if (isNull(foreldreansvar.getNyAnsvarlig())) {
            foreldreansvar.setNyAnsvarlig(new PersonRequestDTO());
        }
        if (isNull(foreldreansvar.getNyAnsvarlig().getAlder()) &&
            isNull(foreldreansvar.getNyAnsvarlig().getFoedtEtter()) &&
            isNull(foreldreansvar.getNyAnsvarlig().getFoedtFoer())) {

            foreldreansvar.getNyAnsvarlig().setFoedtFoer(LocalDateTime.now().minusYears(30));
            foreldreansvar.getNyAnsvarlig().setFoedtEtter(LocalDateTime.now().minusYears(60));
        }
        if (isNull(foreldreansvar.getNyAnsvarlig().getKjoenn())) {
            foreldreansvar.getNyAnsvarlig().setKjoenn(KjoennUtility.getKjoenn());
        }
        EgenskaperFraHovedperson.kopierData(barn, foreldreansvar.getNyAnsvarlig());

        return createPersonService.execute(foreldreansvar.getNyAnsvarlig())
                .doOnNext(createdPerson -> foreldreansvar.setAnsvarlig(createdPerson.getIdent()))
                .then();
    }

    private Mono<Void> setRelasjoner(ForeldreansvarDTO foreldreansvar, PersonDTO barn, Rolle... roller) {

        return Flux.fromIterable(barn.getForelderBarnRelasjon())
                .filter(relasjon -> List.of(roller).contains(relasjon.getRelatertPersonsRolle()))
                .doOnNext(relasjon -> foreldreansvar.setAnsvarlig(relasjon.getRelatertPerson()))
                .flatMap(relasjon -> {
                    if (isNotBlank(foreldreansvar.getAnsvarlig())) {
                        return relasjonService.setRelasjoner(barn.getIdent(), FORELDREANSVAR_FORELDER, foreldreansvar.getAnsvarlig(), FORELDREANSVAR_BARN)
                                .thenReturn(relasjon);
                    }
                    return Mono.just(relasjon);
                })
                .flatMap(relasjon -> oppdaterRelatertAnsvar(foreldreansvar, barn, relasjon.getRelatertPerson()))
                .then();
    }

    private Mono<Void> oppdaterRelatertAnsvar(ForeldreansvarDTO foreldreansvar, PersonDTO barn, String forelder) {

        return personRepository.findByIdent(forelder)
                .doOnNext(dbPerson -> {
                    var subjektAnsvar = mapperFacade.map(foreldreansvar, ForeldreansvarDTO.class);
                    subjektAnsvar.setNyAnsvarlig(null);
                    subjektAnsvar.setAnsvarlig(null);
                    subjektAnsvar.setAnsvarssubjekt(barn.getIdent());
                    subjektAnsvar.setId(dbPerson.getPerson().getForeldreansvar().size() + 1);
                    dbPerson.getPerson().getForeldreansvar().addFirst(subjektAnsvar);
                })
                .flatMap(personRepository::save)
                .then();
    }

    private Mono<Void> setFellesRelasjoner(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {

        return Flux.fromIterable(barn.getForelderBarnRelasjon())
                .filter(ForelderBarnRelasjonDTO::isBarn)
                .collectList()
                .flatMap(foreldre -> {

                    foreldreansvar.setNyAnsvarlig(null);
                    foreldreansvar.setAnsvarlig(foreldre.get(0).getRelatertPerson());
                    return relasjonService.setRelasjoner(barn.getIdent(), FORELDREANSVAR_FORELDER, foreldreansvar.getAnsvarlig(), FORELDREANSVAR_BARN)
                            .thenReturn(foreldre);
                })
                .flatMap(foreldre -> {

                    var nyttForeldreansvar = mapperFacade.map(foreldreansvar, ForeldreansvarDTO.class);
                    nyttForeldreansvar.setAnsvarlig(foreldre.get(1).getRelatertPerson());
                    return relasjonService.setRelasjoner(barn.getIdent(), FORELDREANSVAR_FORELDER, nyttForeldreansvar.getAnsvarlig(), FORELDREANSVAR_BARN)
                            .thenReturn(nyttForeldreansvar)
                            .zipWith(Mono.just(foreldre));
                })
                .flatMapMany(tuple -> {

                    tuple.getT1().setId(barn.getForeldreansvar().size() + 1);
                    barn.getForeldreansvar().addFirst(tuple.getT1());

                    return Flux.fromIterable(tuple.getT2())
                            .flatMap(forelderBarnRelasjon ->
                                    oppdaterRelatertAnsvar(foreldreansvar, barn, forelderBarnRelasjon.getRelatertPerson()));
                })
                .then();
    }

    private Mono<Void> makeAnsvarligUtenIdentifikator(ForeldreansvarDTO foreldreansvar, PersonDTO person) {

        if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {
            foreldreansvar.setAnsvarligUtenIdentifikator(new RelatertBiPersonDTO());
        }

        if (isBlank(foreldreansvar.getAnsvarligUtenIdentifikator().getStatsborgerskap())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setStatsborgerskap("NOR");
        }

        if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator().getKjoenn())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setKjoenn(KjoennUtility.getKjoenn());
        }

        if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator().getFoedselsdato())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setFoedselsdato(
                    FoedselsdatoUtility.getFoedselsdato(person)
                            .plusDays(RANDOM.nextInt(365)));
        }

        var forespurtNavn =
                nonNull(foreldreansvar.getAnsvarligUtenIdentifikator().getNavn()) ?
                        foreldreansvar.getAnsvarligUtenIdentifikator().getNavn() : new PersonnavnDTO();

        if (isBlank(forespurtNavn.getFornavn()) ||
            isBlank(forespurtNavn.getMellomnavn()) ||
            isBlank(forespurtNavn.getEtternavn())) {

            return genererNavnServiceConsumer.getNavn()
                    .doOnNext(nyttNavn ->
                            foreldreansvar.getAnsvarligUtenIdentifikator().setNavn(
                                    PersonnavnDTO.builder()
                                            .fornavn(blankCheck(forespurtNavn.getFornavn(), nyttNavn.getAdjektiv()))
                                            .etternavn(blankCheck(forespurtNavn.getEtternavn(), nyttNavn.getSubstantiv()))
                                            .mellomnavn(blankCheck(forespurtNavn.getMellomnavn(), nyttNavn.getAdverb()))
                                            .build()))
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Void> setRelasjon(BarnRelasjon barnRelasjon, ForeldreansvarDTO foreldreansvar, String person) {

        barnRelasjon.getBarn().getPerson().getForeldreansvar()
                .addFirst(ForeldreansvarDTO.builder()
                        .ansvar(foreldreansvar.getAnsvar())
                        .ansvarlig(barnRelasjon.getAnsvarlig())
                        .eksisterendePerson(foreldreansvar.getEksisterendePerson())
                        .ansvarligUtenIdentifikator(foreldreansvar.getAnsvarligUtenIdentifikator())
                        .kilde(foreldreansvar.getKilde())
                        .master(foreldreansvar.getMaster())
                        .id(barnRelasjon.getBarn().getPerson().getForeldreansvar().stream()
                                    .max(Comparator.comparing(ForeldreansvarDTO::getId))
                                    .map(ForeldreansvarDTO::getId)
                                    .orElse(0) + 1)
                        .gyldigFraOgMed(foreldreansvar.getGyldigFraOgMed())
                        .gyldigTilOgMed(foreldreansvar.getGyldigTilOgMed())
                        .build());

        return Mono.defer(() -> {

                    if (isNotBlank(barnRelasjon.getAnsvarlig())) {
                        foreldreansvar.setAnsvarssubjekt(barnRelasjon.getBarn().getIdent());
                        foreldreansvar.setNyAnsvarlig(null);
                        return relasjonService.setRelasjoner(barnRelasjon.getBarn().getIdent(), FORELDREANSVAR_FORELDER, barnRelasjon.getAnsvarlig(), FORELDREANSVAR_BARN)
                                .thenReturn(barnRelasjon);
                    }
                    return Mono.just(barnRelasjon);
                })
                .flatMap(relasjon -> {

                    if (!person.equals(relasjon.getAnsvarlig())) {
                        return personRepository.findByIdent(relasjon.getAnsvarlig())
                                .doOnNext(ansvarlig -> ansvarlig.getPerson().getForeldreansvar()
                                        .addFirst(ForeldreansvarDTO.builder()
                                                .ansvar(foreldreansvar.getAnsvar())
                                                .ansvarssubjekt(relasjon.getBarn().getIdent())
                                                .gyldigFraOgMed(foreldreansvar.getGyldigFraOgMed())
                                                .gyldigTilOgMed(foreldreansvar.getGyldigTilOgMed())
                                                .kilde(foreldreansvar.getKilde())
                                                .master(foreldreansvar.getMaster())
                                                .id(ansvarlig.getPerson().getForeldreansvar().stream()
                                                            .max(Comparator.comparing(ForeldreansvarDTO::getId))
                                                            .map(ForeldreansvarDTO::getId)
                                                            .orElse(0) + 1)
                                                .build()))
                                .flatMap(personRepository::save)
                                .then();
                    }
                    return Mono.empty();
                })
                .then();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BarnRelasjon {

        private DbPerson barn;
        private String ansvarlig;
    }
}