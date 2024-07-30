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
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForeldreansvarDTO.Ansvar;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonnavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelatertBiPersonDTO;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility.isSyntetisk;
import static no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FAR;
import static no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FORELDER;
import static no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MEDMOR;
import static no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MOR;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_BARN;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
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

    private static final Random random = new SecureRandom();
    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final MapperFacade mapperFacade;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    public List<ForeldreansvarDTO> convert(PersonDTO person) {

        for (var type : person.getForeldreansvar()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
                if (person.getFoedselsdato().stream()
                        .anyMatch(alder -> alder.getFoedselsdato().isBefore(LocalDateTime.now().minusYears(MYNDIG_ALDER))) ||
                        person.getFoedsel().stream()
                                .anyMatch(alder -> alder.getFoedselsdato().isBefore(LocalDateTime.now().minusYears(MYNDIG_ALDER)))) {
                    // hovedperson er voksen
                    handle(type, person);
                } else {
                    // hovedperson er barn
                    handleBarn(type, person);
                }
            }
        }

        return person.getForeldreansvar();
    }

    @Override
    public void validate(ForeldreansvarDTO foreldreansvar, PersonDTO hovedperson) {

        validateForeldreansvar(foreldreansvar);

        if ((foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) &&
                isNull(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                !isRelasjonMor(hovedperson)) {
            throw new InvalidRequestException(INVALID_RELASJON_MOR_EXCEPTION);
        }

        if ((foreldreansvar.getAnsvar() == Ansvar.FAR) &&
                isNull(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                !isRelasjonFar(hovedperson)) {
            throw new InvalidRequestException(INVALID_RELASJON_FAR_EXCEPTION);
        }

        if ((foreldreansvar.getAnsvar() == Ansvar.FELLES) &&
                isNull(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                !isRelasjonForeldre(hovedperson)) {
            throw new InvalidRequestException(INVALID_RELASJON_FELLES_EXCEPTION);
        }
    }

    private void validateForeldreansvar(ForeldreansvarDTO foreldreansvar) {

        if (isNull(foreldreansvar.getAnsvar())) {
            throw new InvalidRequestException(INVALID_EMPTY_ANSVAR_EXCEPTION);
        }

        if (nonNull(foreldreansvar.getAnsvarlig()) &&
                nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

            throw new InvalidRequestException(INVALID_AMBIGUOUS_ANSVARLIG_EXCEPTION);
        }

        if (isNotBlank(foreldreansvar.getAnsvarlig()) &&
                !personRepository.existsByIdent(foreldreansvar.getAnsvarlig())) {

            throw new InvalidRequestException(String.format(INVALID_ANSVARLIG_PERSON_EXCEPTION,
                    foreldreansvar.getAnsvarlig()));
        }

        if (nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

            var navn = foreldreansvar.getAnsvarligUtenIdentifikator().getNavn();
            if (nonNull(navn) && (isNotBlank(navn.getFornavn()) ||
                    isNotBlank(navn.getMellomnavn()) ||
                    isNotBlank(navn.getEtternavn())) &&
                    isFalse(genererNavnServiceConsumer.verifyNavn(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                            .adjektiv(navn.getFornavn())
                            .adverb(navn.getMellomnavn())
                            .substantiv(navn.getEtternavn())
                            .build()))) {

                throw new InvalidRequestException(INVALID_NAVN_ERROR);
            }
        }
    }

    public void validateBarn(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {

        validateForeldreansvar(foreldreansvar);
        if ((foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) &&
                isNull(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                !isRelasjonFraBarn(barn, MOR, MEDMOR)) {
            throw new InvalidRequestException(INVALID_RELASJON_MOR_EXCEPTION);
        }

        if ((foreldreansvar.getAnsvar() == Ansvar.FAR) &&
                isNull(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                !isRelasjonFraBarn(barn, FAR)) {
            throw new InvalidRequestException(INVALID_RELASJON_FAR_EXCEPTION);
        }

        if ((foreldreansvar.getAnsvar() == Ansvar.FELLES) &&
                isNull(foreldreansvar.getAnsvarlig()) && isNull(foreldreansvar.getAnsvarligUtenIdentifikator()) &&
                !isRelasjonForeldreFraBarn(barn)) {
            throw new InvalidRequestException(INVALID_RELASJON_FELLES_EXCEPTION);
        }
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

    private boolean isRelasjonMor(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .anyMatch(relasjon ->
                        MOR == relasjon.getMinRolleForPerson() ||
                                MEDMOR == relasjon.getMinRolleForPerson() ||

                                (FORELDER == relasjon.getMinRolleForPerson() &&
                                        (Kjoenn.KVINNE == hovedperson.getKjoenn().stream().findFirst().orElse(new KjoennDTO()).getKjoenn() ||
                                                Kjoenn.KVINNE == KjoennFraIdentUtility.getKjoenn(hovedperson.getIdent()))) ||

                                isNotTrue(relasjon.getPartnerErIkkeForelder()) &&
                                        hovedperson.getSivilstand().stream()
                                                .anyMatch(sivilstand -> (sivilstand.isGift() || sivilstand.isSeparert())));
    }

    private List<BarnRelasjon> getBarnMorRelasjoner(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .map(barnRelasjon ->
                        personRepository.findByIdent(barnRelasjon.getRelatertPerson())
                                .map(barn -> barn.getPerson().getForelderBarnRelasjon().stream()
                                        .filter(foreldreRelasjon -> foreldreRelasjon.getRelatertPersonsRolle() == Rolle.MOR ||
                                                foreldreRelasjon.getRelatertPersonsRolle() == Rolle.MEDMOR)
                                        .map(foreldreRelasjon -> BarnRelasjon.builder()
                                                .barn(barn)
                                                .ansvarlig(foreldreRelasjon.getRelatertPerson())
                                                .build())
                                        .toList()))
                .flatMap(Optional::stream)
                .flatMap(Collection::stream)
                .toList();
    }

    private boolean isRelasjonFar(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .anyMatch(relasjon -> FAR == relasjon.getMinRolleForPerson() ||

                        (FORELDER == relasjon.getMinRolleForPerson() &&
                                (Kjoenn.MANN == hovedperson.getKjoenn().stream().findFirst().orElse(new KjoennDTO()).getKjoenn() ||
                                        Kjoenn.MANN == KjoennFraIdentUtility.getKjoenn(hovedperson.getIdent()))) ||

                        isNotTrue(relasjon.getPartnerErIkkeForelder()) &&
                                hovedperson.getSivilstand().stream()
                                        .anyMatch(sivilstand -> sivilstand.isGift() || sivilstand.isSeparert()));
    }

    private List<BarnRelasjon> getBarnFarRelasjoner(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .map(barnRelasjon ->
                        personRepository.findByIdent(barnRelasjon.getRelatertPerson())
                                .map(barn -> barn.getPerson().getForelderBarnRelasjon().stream()
                                        .filter(foreldreRelasjon -> foreldreRelasjon.getRelatertPersonsRolle() == Rolle.FAR)
                                        .map(foreldreRelasjon -> BarnRelasjon.builder()
                                                .barn(barn)
                                                .ansvarlig(foreldreRelasjon.getRelatertPerson())
                                                .build())
                                        .toList()))
                .flatMap(Optional::stream)
                .flatMap(Collection::stream)
                .toList();
    }

    private void handle(ForeldreansvarDTO foreldreansvar, PersonDTO hovedperson) {

        if (isNotBlank(foreldreansvar.getAnsvarssubjekt())) {

            personRepository.findByIdent(foreldreansvar.getAnsvarssubjekt())
                    .ifPresent(barn -> {
                        barn.getPerson().getForeldreansvar()
                                .addFirst(ForeldreansvarDTO.builder()
                                        .eksisterendePerson(true)
                                        .ansvar(foreldreansvar.getAnsvar())
                                        .ansvarlig(hovedperson.getIdent())
                                        .gyldigFraOgMed(foreldreansvar.getGyldigFraOgMed())
                                        .gyldigFraOgMed(foreldreansvar.getGyldigTilOgMed())
                                        .kilde(foreldreansvar.getKilde())
                                        .master(foreldreansvar.getMaster())
                                        .id(barn.getPerson().getForeldreansvar().stream()
                                                .map(ForeldreansvarDTO::getId)
                                                .findFirst().orElse(0) + 1)
                                        .build());

                        relasjonService.setRelasjon(barn.getIdent(), hovedperson.getIdent(), FORELDREANSVAR_FORELDER);
                        relasjonService.setRelasjon(hovedperson.getIdent(), barn.getIdent(), FORELDREANSVAR_BARN);
                    });
        }

        if (foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) {
            var barnMorRelasjoner = getBarnMorRelasjoner(hovedperson);
            setRelasjoner(barnMorRelasjoner, foreldreansvar, hovedperson.getIdent());

            slettRelasjonForHovedperson(hovedperson, foreldreansvar.getId(), barnMorRelasjoner);

        } else if (foreldreansvar.getAnsvar() == Ansvar.FAR) {
            var barnFarRelasjoner = getBarnFarRelasjoner(hovedperson);
            setRelasjoner(barnFarRelasjoner, foreldreansvar, hovedperson.getIdent());

            slettRelasjonForHovedperson(hovedperson, foreldreansvar.getId(), barnFarRelasjoner);

        } else if (foreldreansvar.getAnsvar() == Ansvar.FELLES) {
            var barnFellesRelasjoner = new ArrayList<>(getBarnMorRelasjoner(hovedperson));
            barnFellesRelasjoner.addAll(getBarnFarRelasjoner(hovedperson));
            setRelasjoner(barnFellesRelasjoner, foreldreansvar, hovedperson.getIdent());

        } else if (foreldreansvar.getAnsvar() == Ansvar.ANDRE) {

            foreldreansvar.setEksisterendePerson(isNotBlank(foreldreansvar.getAnsvarlig()));

            if (nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

                makeAnsvarligUtenIdentifikator(foreldreansvar, hovedperson);

            } else if (isNull(foreldreansvar.getAnsvarlig())) {

                opprettNyAsvarlig(foreldreansvar, hovedperson);
            }

            setRelasjoner(getBarnRelasjoner(foreldreansvar, hovedperson), foreldreansvar, hovedperson.getIdent());
            slettForeldreansvar(hovedperson, foreldreansvar.getId());

        } else if (foreldreansvar.getAnsvar() == Ansvar.UKJENT) {

            makeAnsvarligUtenIdentifikator(foreldreansvar, hovedperson);
            setRelasjoner(getBarnRelasjoner(foreldreansvar, hovedperson), foreldreansvar, hovedperson.getIdent());
            slettForeldreansvar(hovedperson, foreldreansvar.getId());
        }
    }

    private static void slettRelasjonForHovedperson(PersonDTO hovedperson, Integer id, List<BarnRelasjon> barnForelderRelasjoner) {

        if (barnForelderRelasjoner.stream()
                .noneMatch(relasjon -> relasjon.getAnsvarlig().equals(hovedperson.getIdent()))) {

            slettForeldreansvar(hovedperson, id);
        }
    }

    private static void slettForeldreansvar(PersonDTO person, Integer id) {

        person.setForeldreansvar(person.getForeldreansvar().stream()
                .filter(ansvar -> !Objects.equals(id, ansvar.getId()))
                .toList());
    }

    private List<BarnRelasjon> getBarnRelasjoner(ForeldreansvarDTO foreldreansvar, PersonDTO hovedperson) {
        return hovedperson.getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::hasBarn)
                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                .map(personRepository::findByIdent)
                .flatMap(Optional::stream)
                .map(dbperson -> BarnRelasjon.builder()
                        .ansvarlig(foreldreansvar.getAnsvarlig())
                        .barn(dbperson)
                        .build())
                .toList();
    }

    public void handleBarn(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {

        if (isNotBlank(foreldreansvar.getAnsvarlig())) {

            foreldreansvar.setEksisterendePerson(isTrue(foreldreansvar.getEksisterendePerson()) ||
                    foreldreansvar.getAnsvar() == Ansvar.ANDRE ||
                    foreldreansvar.getAnsvar() == Ansvar.UKJENT);
            relasjonService.setRelasjon(barn.getIdent(), foreldreansvar.getAnsvarlig(), FORELDREANSVAR_FORELDER);
            relasjonService.setRelasjon(foreldreansvar.getAnsvarlig(), barn.getIdent(), FORELDREANSVAR_BARN);

        } else if (foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) {

            setRelasjoner(foreldreansvar, barn, MOR, MEDMOR);

        } else if (foreldreansvar.getAnsvar() == Ansvar.FAR) {

            setRelasjoner(foreldreansvar, barn, FAR);

        } else if (foreldreansvar.getAnsvar() == Ansvar.FELLES) {

            setFellesRelasjoner(foreldreansvar, barn);

        } else if (foreldreansvar.getAnsvar() == Ansvar.ANDRE) {

            foreldreansvar.setEksisterendePerson(isNotBlank(foreldreansvar.getAnsvarlig()));

            if (nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

                setAnsvarUtenIdentifikator(foreldreansvar, barn);

            } else if (isNull(foreldreansvar.getAnsvarlig())) {

                opprettNyAsvarlig(foreldreansvar, barn);
            }

            if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {
                relasjonService.setRelasjon(barn.getIdent(), foreldreansvar.getAnsvarlig(), FORELDREANSVAR_FORELDER);
                relasjonService.setRelasjon(foreldreansvar.getAnsvarlig(), barn.getIdent(), FORELDREANSVAR_BARN);
            }
        } else if (foreldreansvar.getAnsvar() == Ansvar.UKJENT) {

            setAnsvarUtenIdentifikator(foreldreansvar, barn);
        }

        foreldreansvar.setNyAnsvarlig(null);
    }

    private void setAnsvarUtenIdentifikator(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {
        barn.getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isBarn)
                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                .findFirst()
                .flatMap(personRepository::findByIdent)
                .ifPresent(person -> makeAnsvarligUtenIdentifikator(foreldreansvar, person.getPerson()));
    }

    private void opprettNyAsvarlig(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {
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
            foreldreansvar.getNyAnsvarlig().setKjoenn(random.nextBoolean() ? Kjoenn.KVINNE : Kjoenn.MANN);
        }
        if (isNull(foreldreansvar.getNyAnsvarlig().getSyntetisk())) {
            foreldreansvar.getNyAnsvarlig().setSyntetisk(isSyntetisk(barn.getIdent()));
        }

        PersonDTO relatertPerson = createPersonService.execute(foreldreansvar.getNyAnsvarlig());

        foreldreansvar.setAnsvarlig(relatertPerson.getIdent());
    }

    private void setRelasjoner(ForeldreansvarDTO foreldreansvar, PersonDTO barn, Rolle... roller) {

        barn.getForelderBarnRelasjon().stream()
                .filter(relasjon -> List.of(roller).contains(relasjon.getRelatertPersonsRolle()))
                .findFirst()
                .ifPresent(relasjon -> {
                    foreldreansvar.setAnsvarlig(relasjon.getRelatertPerson());

                    if (isNotBlank(foreldreansvar.getAnsvarlig())) {
                        relasjonService.setRelasjon(barn.getIdent(), foreldreansvar.getAnsvarlig(), FORELDREANSVAR_FORELDER);
                        relasjonService.setRelasjon(foreldreansvar.getAnsvarlig(), barn.getIdent(), FORELDREANSVAR_BARN);
                    }
                });
    }

    private void setFellesRelasjoner(ForeldreansvarDTO foreldreansvar, PersonDTO barn) {

        var foreldre = barn.getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isBarn)
                .toList();
        foreldreansvar.setAnsvarlig(foreldre.getFirst().getRelatertPerson());
        relasjonService.setRelasjon(barn.getIdent(), foreldreansvar.getAnsvarlig(), FORELDREANSVAR_FORELDER);
        relasjonService.setRelasjon(foreldreansvar.getAnsvarlig(), barn.getIdent(), FORELDREANSVAR_BARN);

        var nyttForeldreansvar = mapperFacade.map(foreldreansvar, ForeldreansvarDTO.class);
        nyttForeldreansvar.setAnsvarlig(foreldre.get(1).getRelatertPerson());
        relasjonService.setRelasjon(barn.getIdent(), nyttForeldreansvar.getAnsvarlig(), FORELDREANSVAR_FORELDER);
        relasjonService.setRelasjon(nyttForeldreansvar.getAnsvarlig(), barn.getIdent(), FORELDREANSVAR_BARN);
        nyttForeldreansvar.setId(barn.getForeldreansvar().size() + 1);
        barn.getForeldreansvar().addFirst(nyttForeldreansvar);
    }

    private void makeAnsvarligUtenIdentifikator(ForeldreansvarDTO foreldreansvar, PersonDTO person) {

        if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {
            foreldreansvar.setAnsvarligUtenIdentifikator(new RelatertBiPersonDTO());
        }

        if (isBlank(foreldreansvar.getAnsvarligUtenIdentifikator().getStatsborgerskap())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setStatsborgerskap("NOR");
        }

        var forespurtNavn =
                nonNull(foreldreansvar.getAnsvarligUtenIdentifikator().getNavn()) ?
                        foreldreansvar.getAnsvarligUtenIdentifikator().getNavn() : new PersonnavnDTO();

        if (isBlank(forespurtNavn.getFornavn()) ||
                isBlank(forespurtNavn.getMellomnavn()) ||
                isBlank(forespurtNavn.getEtternavn())) {

            genererNavnServiceConsumer.getNavn(1)
                    .ifPresent(nyttNavn ->
                            foreldreansvar.getAnsvarligUtenIdentifikator().setNavn(
                                    PersonnavnDTO.builder()
                                            .fornavn(blankCheck(forespurtNavn.getFornavn(), nyttNavn.getAdjektiv()))
                                            .etternavn(blankCheck(forespurtNavn.getEtternavn(), nyttNavn.getSubstantiv()))
                                            .mellomnavn(blankCheck(forespurtNavn.getMellomnavn(), nyttNavn.getAdverb()))
                                            .build()));
        }

        if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator().getKjoenn())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setKjoenn(random.nextBoolean() ? Kjoenn.MANN : Kjoenn.KVINNE);
        }

        if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator().getFoedselsdato())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setFoedselsdato(
                    FoedselsdatoUtility.getFoedselsdato(person)
                            .plusDays(random.nextInt(365)));
        }
    }

    private void setRelasjoner(List<BarnRelasjon> barnRelasjoner, ForeldreansvarDTO foreldreansvar, String hovedperson) {

        barnRelasjoner.forEach(relasjon -> {

            relasjon.getBarn().getPerson().getForeldreansvar()
                    .addFirst(ForeldreansvarDTO.builder()
                            .ansvar(foreldreansvar.getAnsvar())
                            .ansvarlig(relasjon.getAnsvarlig())
                            .eksisterendePerson(foreldreansvar.getEksisterendePerson())
                            .ansvarligUtenIdentifikator(foreldreansvar.getAnsvarligUtenIdentifikator())
                            .kilde(foreldreansvar.getKilde())
                            .master(foreldreansvar.getMaster())
                            .id(relasjon.getBarn().getPerson().getForeldreansvar().stream()
                                    .max(Comparator.comparing(ForeldreansvarDTO::getId))
                                    .map(ForeldreansvarDTO::getId)
                                    .orElse(0) + 1)
                            .gyldigFraOgMed(foreldreansvar.getGyldigFraOgMed())
                            .gyldigTilOgMed(foreldreansvar.getGyldigTilOgMed())
                            .build());

            if (isNotBlank(relasjon.getAnsvarlig())) {
                foreldreansvar.setAnsvarssubjekt(relasjon.getBarn().getIdent());
                foreldreansvar.setNyAnsvarlig(null);
                relasjonService.setRelasjon(relasjon.getBarn().getIdent(), relasjon.getAnsvarlig(), FORELDREANSVAR_FORELDER);
                relasjonService.setRelasjon(relasjon.getAnsvarlig(), relasjon.getBarn().getIdent(), FORELDREANSVAR_BARN);
            }

            if (!hovedperson.equals(relasjon.getAnsvarlig())) {
                personRepository.findByIdent(relasjon.ansvarlig)
                        .ifPresent(ansvarlig -> ansvarlig.getPerson().getForeldreansvar()
                                .addFirst(ForeldreansvarDTO.builder()
                                        .ansvarssubjekt(relasjon.getBarn().getIdent())
                                        .gyldigFraOgMed(foreldreansvar.getGyldigFraOgMed())
                                        .gyldigTilOgMed(foreldreansvar.getGyldigTilOgMed())
                                        .kilde(foreldreansvar.getKilde())
                                        .master(foreldreansvar.getMaster())
                                        .id(ansvarlig.getPerson().getForeldreansvar().stream()
                                                .max(Comparator.comparing(ForeldreansvarDTO::getId))
                                                .map(ForeldreansvarDTO::getId)
                                                .orElse(0) + 1)
                                        .build()));
            }
        });
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