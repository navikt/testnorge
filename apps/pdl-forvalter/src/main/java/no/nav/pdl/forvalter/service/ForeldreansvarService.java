package no.nav.pdl.forvalter.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO.Ansvar;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO.PersonnavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility.isSyntetisk;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FAR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MEDMOR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MOR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ForeldreansvarService implements BiValidation<ForeldreansvarDTO, PersonDTO> {

    private static final String INVALID_EMPTY_ANSVAR_EXCEPTION = "Forelderansvar: hvem som har ansvar må oppgis";
    private static final String INVALID_AMBIGUOUS_ANSVARLIG_EXCEPTION = "Forelderansvar: kun et av feltene 'ansvarlig' " +
            "og 'ansvarligUtenIdentifikator' kan benyttes";
    private static final String INVALID_ANSVARLIG_PERSON_EXCEPTION = "Foreldreansvar: Ansvarlig person %s finnes ikke";
    private static final String INVALID_NAVN_ERROR = "Foreldreansvar: Navn er ikke i liste over gyldige verdier";
    private static final String INVALID_RELASJON_MOR_EXCEPTION = "Foreldreansvar: barn mangler / " +
            "barnets foreldrerelasjon til mor ikke funnet";
    private static final String INVALID_RELASJON_FAR_EXCEPTION = "Foreldreansvar: barn mangler / " +
            "barnets foreldrerelasjon til far ikke funnet";
    private static final String INVALID_RELASJON_FELLES_EXCEPTION = "Foreldreansvar: barn mangler / " +
            "barnets foreldrerelasjon til mor og/eller far ikke funnet";

    private final static Random random = new SecureRandom();
    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    public List<ForeldreansvarDTO> convert(PersonDTO person) {

        for (var type : person.getForeldreansvar()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
                handle(type, person);
            }
        }
        // Foreldreanvar settes kun på barn ikke på foreldre
        return emptyList();
    }

    @Override
    public void validate(ForeldreansvarDTO foreldreansvar, PersonDTO hovedperson) {

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

    private boolean isRelasjonForeldre(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .anyMatch(ForelderBarnRelasjonDTO::isForeldre) &&

                (personRepository.findByIdent(hovedperson.getSivilstand().stream()
                                .filter(sivilstand -> sivilstand.isGift() || sivilstand.isSeparert())
                                .map(SivilstandDTO::getRelatertVedSivilstand)
                                .filter(Objects::nonNull)
                                .findFirst().orElse("0"))
                        .orElse(DbPerson.builder().person(new PersonDTO()).build())
                        .getPerson().getForelderBarnRelasjon().stream()
                        .anyMatch(ForelderBarnRelasjonDTO::isForeldre) ||

                        hovedperson.getSivilstand().stream()
                                .anyMatch(sivilstand -> sivilstand.isGift() || sivilstand.isSeparert()));
    }

    private boolean isRelasjonMor(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .anyMatch(relasjon ->
                        MOR == relasjon.getMinRolleForPerson() ||
                                MEDMOR == relasjon.getMinRolleForPerson() ||
                                (FORELDER == relasjon.getMinRolleForPerson() &&
                                        Kjoenn.KVINNE == hovedperson.getKjoenn().stream().findFirst().orElse(new KjoennDTO()).getKjoenn())) ||
                isPartnerMor(hovedperson);
    }

    private boolean isPartnerMor(PersonDTO hovedperson) {

        return personRepository.findByIdent(hovedperson.getSivilstand().stream()
                        .filter(sivilstand -> sivilstand.isGift() || sivilstand.isSeparert())
                        .map(SivilstandDTO::getRelatertVedSivilstand)
                        .findFirst().orElse("0"))
                .orElse(DbPerson.builder().person(new PersonDTO()).build())
                .getPerson().getForelderBarnRelasjon().stream()
                .anyMatch(relasjon -> MOR == relasjon.getMinRolleForPerson() ||
                        MEDMOR == relasjon.getMinRolleForPerson());
    }

    private List<BarnRelasjon> getBarnMorRelasjoner(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .map(barnRelasjon -> {
                    DbPerson barn = personRepository.findByIdent(barnRelasjon.getRelatertPerson()).get();
                    return barn.getPerson().getForelderBarnRelasjon().stream()
                            .filter(foreldreRelasjon -> foreldreRelasjon.getRelatertPersonsRolle() == Rolle.MOR ||
                                    foreldreRelasjon.getRelatertPersonsRolle() == Rolle.MEDMOR)
                            .map(foreldreRelasjon -> BarnRelasjon.builder()
                                    .barn(barn)
                                    .ansvarlig(foreldreRelasjon.getRelatertPerson())
                                    .build())
                            .toList();
                })
                .flatMap(Collection::stream)
                .toList();
    }

    private boolean isRelasjonFar(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .anyMatch(relasjon ->
                        MEDMOR != relasjon.getMinRolleForPerson() &&
                                (FAR == relasjon.getMinRolleForPerson() ||
                                        (FORELDER == relasjon.getMinRolleForPerson() &&
                                                Kjoenn.MANN == hovedperson.getKjoenn().stream().findFirst()
                                                        .orElse(new KjoennDTO()).getKjoenn()) ||
                                        isPartnerFar(hovedperson)));
    }

    private boolean isPartnerFar(PersonDTO hovedperson) {

        return personRepository.findByIdent(hovedperson.getSivilstand().stream()
                        .filter(sivilstand -> sivilstand.isGift() || sivilstand.isSeparert())
                        .map(SivilstandDTO::getRelatertVedSivilstand)
                        .findFirst().orElse("0"))
                .orElse(DbPerson.builder().person(new PersonDTO()).build())
                .getPerson().getForelderBarnRelasjon().stream()
                .anyMatch(relasjon -> FAR == relasjon.getMinRolleForPerson());
    }

    private List<BarnRelasjon> getBarnFarRelasjoner(PersonDTO hovedperson) {

        return hovedperson.getForelderBarnRelasjon().stream()
                .map(barnRelasjon -> {
                    DbPerson barn = personRepository.findByIdent(barnRelasjon.getRelatertPerson()).get();
                    return barn.getPerson().getForelderBarnRelasjon().stream()
                            .filter(foreldreRelasjon -> foreldreRelasjon.getRelatertPersonsRolle() == Rolle.FAR)
                            .map(foreldreRelasjon -> BarnRelasjon.builder()
                                    .barn(barn)
                                    .ansvarlig(foreldreRelasjon.getRelatertPerson())
                                    .build())
                            .toList();
                })
                .flatMap(Collection::stream)
                .toList();
    }

    private void handle(ForeldreansvarDTO foreldreansvar, PersonDTO hovedperson) {

        if (foreldreansvar.getAnsvar() == Ansvar.MOR || foreldreansvar.getAnsvar() == Ansvar.MEDMOR) {
            var barnMorRelasjoner = getBarnMorRelasjoner(hovedperson);
            setRelasjoner(barnMorRelasjoner, foreldreansvar);

        } else if (foreldreansvar.getAnsvar() == Ansvar.FAR) {
            var barnFarRelasjoner = getBarnFarRelasjoner(hovedperson);
            setRelasjoner(barnFarRelasjoner, foreldreansvar);

        } else if (foreldreansvar.getAnsvar() == Ansvar.FELLES) {
            var barnFellesRelasjoner = new ArrayList<>(getBarnMorRelasjoner(hovedperson));
            barnFellesRelasjoner.addAll(getBarnFarRelasjoner(hovedperson));
            setRelasjoner(barnFellesRelasjoner, foreldreansvar);

        } else if (foreldreansvar.getAnsvar() == Ansvar.ANDRE) {
            if (nonNull(foreldreansvar.getAnsvarligUtenIdentifikator())) {

                makeAnsvarligUtenIdentifier(foreldreansvar, hovedperson);

            } else if (isNull(foreldreansvar.getAnsvarlig())) {

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
                    foreldreansvar.getNyAnsvarlig().setSyntetisk(isSyntetisk(hovedperson.getIdent()));
                }

                PersonDTO relatertPerson = createPersonService.execute(foreldreansvar.getNyAnsvarlig());

                foreldreansvar.setAnsvarlig(relatertPerson.getIdent());
            }

            setRelasjoner(hovedperson.getForelderBarnRelasjon().stream()
                    .filter(relasjon -> relasjon.getRelatertPersonsRolle() == Rolle.BARN)
                    .map(relasjon -> relasjon.getRelatertPerson())
                    .map(ident -> personRepository.findByIdent(ident))
                    .map(dbperson -> BarnRelasjon.builder()
                            .ansvarlig(foreldreansvar.getAnsvarlig())
                            .barn(dbperson.get())
                            .build())
                    .collect(Collectors.toList()), foreldreansvar);
        }

        foreldreansvar.setNyAnsvarlig(null);
    }

    private void makeAnsvarligUtenIdentifier(ForeldreansvarDTO foreldreansvar, PersonDTO hovedperson) {

        if (isBlank(foreldreansvar.getAnsvarligUtenIdentifikator().getStatsborgerskap())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setStatsborgerskap("NOR");
        }

        var forespurtNavn =
                nonNull(foreldreansvar.getAnsvarligUtenIdentifikator().getNavn()) ?
                        foreldreansvar.getAnsvarligUtenIdentifikator().getNavn() : new PersonnavnDTO();

        if (isBlank(forespurtNavn.getFornavn()) ||
                isBlank(forespurtNavn.getMellomnavn()) ||
                isBlank(forespurtNavn.getEtternavn())) {

            var nyttNavn = genererNavnServiceConsumer.getNavn(1);
            if (nyttNavn.isPresent()) {
                foreldreansvar.getAnsvarligUtenIdentifikator().setNavn(
                        PersonnavnDTO.builder()
                                .fornavn(blankCheck(forespurtNavn.getFornavn(), nyttNavn.get().getAdjektiv()))
                                .etternavn(blankCheck(forespurtNavn.getEtternavn(), nyttNavn.get().getSubstantiv()))
                                .mellomnavn(blankCheck(forespurtNavn.getMellomnavn(), nyttNavn.get().getAdverb()))
                                .build());
            }
        }

        if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator().getKjoenn())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setKjoenn(random.nextBoolean() ? Kjoenn.MANN : Kjoenn.KVINNE);
        }

        if (isNull(foreldreansvar.getAnsvarligUtenIdentifikator().getFoedselsdato())) {
            foreldreansvar.getAnsvarligUtenIdentifikator().setFoedselsdato(
                    DatoFraIdentUtility.getDato(hovedperson.getIdent())
                            .plusDays(random.nextInt(365)).atStartOfDay());
        }
    }

    private void setRelasjoner(List<BarnRelasjon> barnRelasjoner, ForeldreansvarDTO foreldreansvar) {

        barnRelasjoner.forEach(relasjon -> {

            relasjon.getBarn().getPerson().getForeldreansvar()
                    .add(0, ForeldreansvarDTO.builder()
                            .ansvar(foreldreansvar.getAnsvar())
                            .ansvarlig(relasjon.getAnsvarlig())
                            .ansvarligUtenIdentifikator(foreldreansvar.getAnsvarligUtenIdentifikator())
                            .kilde(foreldreansvar.getKilde())
                            .master(foreldreansvar.getMaster())
                            .id(relasjon.getBarn().getPerson().getForeldreansvar().stream()
                                    .map(ForeldreansvarDTO::getId)
                                    .findFirst().orElse(0) + 1)
                            .build());

            if (nonNull(relasjon.getAnsvarlig())) {
                relasjonService.setRelasjon(relasjon.getBarn().getIdent(), relasjon.getAnsvarlig(), FORELDREANSVAR);
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