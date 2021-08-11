package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.ROLLE;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand.defaultAdresse;
import static no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility.isSyntetisk;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ForelderBarnRelasjonService {

    private static final String INVALID_EMPTY_MIN_ROLLE_EXCEPTION = "ForelderBarnRelasjon: min rolle for person må oppgis";
    private static final String INVALID_EMPTY_RELATERT_PERSON_ROLLE_EXCEPTION = "ForelderBarnRelasjon: relatert persons rolle må oppgis";
    private static final String AMBIGUOUS_PERSON_ROLLE_EXCEPTION = "ForelderBarnRelasjon: min rolle og relatert persons " +
            "rolle må være av type barn -- forelder, eller forelder -- barn";
    private static final String INVALID_RELATERT_PERSON_EXCEPTION = "ForelderBarnRelasjon: Relatert person %s finnes ikke";

    private static final Random RANDOM = new SecureRandom();

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final MapperFacade mapperFacade;
    private final MergeService mergeService;

    public List<ForelderBarnRelasjonDTO> convert(PersonDTO person) {

        for (var type : person.getForelderBarnRelasjon()) {

            if (isTrue(type.getIsNew())) {
                validate(type);

                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
                handle(type, person);
            }
        }
        return person.getForelderBarnRelasjon();
    }

    private void validate(ForelderBarnRelasjonDTO relasjon) {

        if (isNull(relasjon.getMinRolleForPerson())) {
            throw new InvalidRequestException(INVALID_EMPTY_MIN_ROLLE_EXCEPTION);
        }

        if (isNull(relasjon.getRelatertPersonsRolle())) {
            throw new InvalidRequestException(INVALID_EMPTY_RELATERT_PERSON_ROLLE_EXCEPTION);
        }

        if ((relasjon.getMinRolleForPerson() == ROLLE.BARN && relasjon.getRelatertPersonsRolle() == ROLLE.BARN) ||
                (relasjon.getMinRolleForPerson() != ROLLE.BARN && relasjon.getRelatertPersonsRolle() != ROLLE.BARN)) {
            throw new InvalidRequestException(AMBIGUOUS_PERSON_ROLLE_EXCEPTION);
        }

        if (isNotBlank(relasjon.getRelatertPerson()) &&
                !personRepository.existsByIdent(relasjon.getRelatertPerson())) {

            throw new InvalidRequestException(String.format(INVALID_RELATERT_PERSON_EXCEPTION,
                    relasjon.getRelatertPerson()));
        }
    }

    private void handle(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        setRelatertPerson(relasjon, hovedperson);

        addForelderBarnRelasjon(relasjon, hovedperson);

        if (isNotTrue(relasjon.getPartnerErIkkeForelder()) && hovedperson.getSivilstand().stream()
                .anyMatch(sivilstand -> nonNull(sivilstand.getRelatertVedSivilstand()))) {

            DbPerson partner = hovedperson.getSivilstand().stream()
                    .filter(sivilstand -> nonNull(sivilstand.getRelatertVedSivilstand()))
                    .map(sivilstand -> personRepository.findByIdent(sivilstand.getRelatertVedSivilstand()).get())
                    .findFirst().get();

            partner.getPerson().getForelderBarnRelasjon().add(
                    addForelderBarnRelasjon(mapperFacade.map(relasjon, ForelderBarnRelasjonDTO.class), partner.getPerson()));
            personRepository.save(partner);
        }
    }

    private ForelderBarnRelasjonDTO addForelderBarnRelasjon(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        fixRoller(relasjon, hovedperson);
        relasjonService.setRelasjoner(hovedperson.getIdent(),
                relasjon.getRelatertPersonsRolle() == ROLLE.BARN ? FAMILIERELASJON_FORELDER : FAMILIERELASJON_BARN,
                relasjon.getRelatertPerson(),
                relasjon.getRelatertPersonsRolle() == ROLLE.BARN ? FAMILIERELASJON_BARN : FAMILIERELASJON_FORELDER);

        createMotsattRelasjon(relasjon, hovedperson.getIdent());
        return relasjon;
    }

    private String setRelatertPerson(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        if (isBlank(relasjon.getRelatertPerson())) {

            if (isNull(relasjon.getNyRelatertPerson())) {
                relasjon.setNyRelatertPerson(new PersonRequestDTO());
            }
            if (isNull(relasjon.getNyRelatertPerson().getAlder()) &&
                    isNull(relasjon.getNyRelatertPerson().getFoedtEtter()) &&
                    isNull(relasjon.getNyRelatertPerson().getFoedtFoer())) {

                relasjon.getNyRelatertPerson().setFoedtFoer(LocalDateTime.now().minusYears(
                        relasjon.getMinRolleForPerson() == ROLLE.BARN ? 60 : 0));
                relasjon.getNyRelatertPerson().setFoedtEtter(LocalDateTime.now().minusYears(
                        relasjon.getMinRolleForPerson() == ROLLE.BARN ? 90 : 18));
            }
            if (isNull(relasjon.getNyRelatertPerson().getKjoenn())) {
                relasjon.getNyRelatertPerson().setKjoenn(getKjoenn(relasjon.getRelatertPersonsRolle()));
            }
            if (isNull(relasjon.getNyRelatertPerson().getSyntetisk())) {
                relasjon.getNyRelatertPerson().setSyntetisk(isSyntetisk(hovedperson.getIdent()));
            }

            PersonDTO relatertPerson = createPersonService.execute(relasjon.getNyRelatertPerson());

            if (isNotTrue(relasjon.getBorIkkeSammen()) && !hovedperson.getBostedsadresse().isEmpty()) {
                BostedadresseDTO fellesAdresse = hovedperson.getBostedsadresse().stream()
                        .findFirst()
                        .orElse(BostedadresseDTO.builder()
                                .vegadresse(mapperFacade.map(defaultAdresse(), VegadresseDTO.class))
                                .build());
                fellesAdresse.setGyldigFraOgMed(getMaxDato(getLastFlyttedato(hovedperson), getLastFlyttedato(relatertPerson)));
                relatertPerson.getBostedsadresse().add(0, fellesAdresse);
            }

            relasjon.setRelatertPerson(relatertPerson.getIdent());
        }

        relasjon.setBorIkkeSammen(null);
        relasjon.setNyRelatertPerson(null);
        return relasjon.getRelatertPerson();
    }

    private void fixRoller(ForelderBarnRelasjonDTO relasjon, PersonDTO person) {

        if (relasjon.getRelatertPersonsRolle() == ROLLE.BARN) {
            relasjon.setMinRolleForPerson(getRolle(person));
        }
        if (relasjon.getRelatertPersonsRolle() == ROLLE.FORELDER) {
            //TBD
        }
    }

    private ROLLE getRolle(PersonDTO person) {
        return person.getKjoenn().stream().findFirst()
                .map(KjoennDTO::getKjoenn)
                .orElse(KjoennFraIdentUtility.getKjoenn(person.getIdent())) == MANN ?
                ROLLE.FAR : ROLLE.MOR;
    }

    private LocalDateTime getMaxDato(LocalDateTime dato1, LocalDateTime dato2) {

        return dato1.isAfter(dato2) ? dato1 : dato2;
    }

    private LocalDateTime getLastFlyttedato(PersonDTO person) {

        return person.getBostedsadresse().stream().findFirst()
                .filter(adr -> nonNull(adr.getGyldigFraOgMed()))
                .map(adr -> adr.getGyldigTilOgMed())
                .orElse(DatoFraIdentUtility.getDato(person.getIdent()).atStartOfDay());
    }

    private void createMotsattRelasjon(ForelderBarnRelasjonDTO relasjon, String hovedperson) {

        DbPerson relatertPerson = personRepository.findByIdent(relasjon.getRelatertPerson()).get();
        ForelderBarnRelasjonDTO relatertFamilierelasjon = mapperFacade.map(relasjon, ForelderBarnRelasjonDTO.class);
        relatertFamilierelasjon.setRelatertPerson(hovedperson);
        swapRoller(relatertFamilierelasjon);
        relatertFamilierelasjon.setId(relatertPerson.getPerson().getForelderBarnRelasjon().stream().findFirst()
                .map(ForelderBarnRelasjonDTO::getId)
                .orElse(0) + 1);
        relatertPerson.getPerson().getForelderBarnRelasjon().add(relatertFamilierelasjon);
        mergeService.merge(relatertPerson.getPerson(), relatertPerson.getPerson());
        personRepository.save(relatertPerson);
    }

    private KjoennDTO.Kjoenn getKjoenn(ROLLE rolle) {

        switch (rolle) {
            case FAR:
                return MANN;
            case MOR:
            case MEDMOR:
                return KVINNE;
            case BARN:
            default:
                return RANDOM.nextBoolean() ? MANN : KVINNE;
        }
    }

    private ForelderBarnRelasjonDTO swapRoller(ForelderBarnRelasjonDTO relasjon) {

        ROLLE rolle = relasjon.getMinRolleForPerson();
        relasjon.setMinRolleForPerson(relasjon.getRelatertPersonsRolle());
        relasjon.setRelatertPersonsRolle(rolle);
        return relasjon;
    }
}
