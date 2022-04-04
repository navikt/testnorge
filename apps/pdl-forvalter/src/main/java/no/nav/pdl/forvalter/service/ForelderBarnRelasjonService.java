package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Collections.emptyList;
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
public class ForelderBarnRelasjonService implements Validation<ForelderBarnRelasjonDTO> {

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

    public List<ForelderBarnRelasjonDTO> convert(PersonDTO person) {

        var nyeRelasjoner = new ArrayList<ForelderBarnRelasjonDTO>();
        for (var type : person.getForelderBarnRelasjon()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
                nyeRelasjoner.addAll(handle(type, person));
            }
        }
        person.getForelderBarnRelasjon().addAll(nyeRelasjoner);
        return person.getForelderBarnRelasjon();
    }

    @Override
    public void validate(ForelderBarnRelasjonDTO relasjon) {

        if (isNull(relasjon.getMinRolleForPerson())) {
            throw new InvalidRequestException(INVALID_EMPTY_MIN_ROLLE_EXCEPTION);
        }

        if (isNull(relasjon.getRelatertPersonsRolle())) {
            throw new InvalidRequestException(INVALID_EMPTY_RELATERT_PERSON_ROLLE_EXCEPTION);
        }

        if ((relasjon.getMinRolleForPerson() == Rolle.BARN && relasjon.getRelatertPersonsRolle() == Rolle.BARN) ||
                (relasjon.getMinRolleForPerson() != Rolle.BARN && relasjon.getRelatertPersonsRolle() != Rolle.BARN)) {
            throw new InvalidRequestException(AMBIGUOUS_PERSON_ROLLE_EXCEPTION);
        }

        if (isNotBlank(relasjon.getRelatertPerson()) &&
                !personRepository.existsByIdent(relasjon.getRelatertPerson())) {

            throw new InvalidRequestException(String.format(INVALID_RELATERT_PERSON_EXCEPTION,
                    relasjon.getRelatertPerson()));
        }
    }

    private List<ForelderBarnRelasjonDTO> handle(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        var request = mapperFacade.map(relasjon, ForelderBarnRelasjonDTO.class);
        setRelatertPerson(relasjon, hovedperson);
        addForelderBarnRelasjon(relasjon, hovedperson);

        if (request.getRelatertPersonsRolle() == Rolle.BARN &&
                isNotTrue(request.getPartnerErIkkeForelder()) && hovedperson.getSivilstand().stream()
                .anyMatch(sivilstand -> nonNull(sivilstand.getRelatertVedSivilstand()))) {

            request.setRelatertPerson(relasjon.getRelatertPerson());
            request.setNyRelatertPerson(null);
            request.setBorIkkeSammen(null);
            request.setMinRolleForPerson(switch (request.getMinRolleForPerson()) {
                case FAR, MEDMOR -> Rolle.MOR;
                case MOR -> Rolle.FAR;
                default -> request.getMinRolleForPerson();
            });

            var partner = hovedperson.getSivilstand().stream()
                    .filter(sivilstand -> nonNull(sivilstand.getRelatertVedSivilstand()))
                    .map(sivilstand -> personRepository.findByIdent(sivilstand.getRelatertVedSivilstand())
                            .orElseThrow(() -> new NotFoundException("Partner ikke funnet " + sivilstand.getRelatertVedSivilstand())))
                    .findFirst();
            if (partner.isPresent()) {
                partner.get().getPerson().getForelderBarnRelasjon().add(0,
                        addForelderBarnRelasjon(request, partner.get().getPerson()));
                personRepository.save(partner.get());
            }
        }
        relasjon.setPartnerErIkkeForelder(null);

        if (request.getMinRolleForPerson() == Rolle.BARN && request.getRelatertPersonsRolle() == Rolle.FORELDER) {
            ForelderBarnRelasjonDTO forelderRelasjon = mapperFacade.map(request, ForelderBarnRelasjonDTO.class);
            forelderRelasjon.setNyRelatertPerson(PersonRequestDTO.builder()
                    .kjoenn(KjoennFraIdentUtility.getKjoenn(relasjon.getRelatertPerson()) == MANN ? KVINNE : MANN)
                    .build());
            forelderRelasjon.setRelatertPerson(null);

            setRelatertPerson(forelderRelasjon, hovedperson);
            addForelderBarnRelasjon(forelderRelasjon, hovedperson);
            forelderRelasjon.setId(hovedperson.getForelderBarnRelasjon().stream()
                    .map(ForelderBarnRelasjonDTO::getId)
                    .findFirst()
                    .orElse(0) + 1);
            return List.of(forelderRelasjon);
        }
        return emptyList();
    }

    private ForelderBarnRelasjonDTO addForelderBarnRelasjon(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        getRolle(relasjon, hovedperson);
        relasjonService.setRelasjoner(hovedperson.getIdent(),
                relasjon.getRelatertPersonsRolle() == Rolle.BARN ? FAMILIERELASJON_FORELDER : FAMILIERELASJON_BARN,
                relasjon.getRelatertPerson(),
                relasjon.getRelatertPersonsRolle() == Rolle.BARN ? FAMILIERELASJON_BARN : FAMILIERELASJON_FORELDER);

        createMotsattRelasjon(relasjon, hovedperson.getIdent());
        return relasjon;
    }

    private String setRelatertPerson(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        relasjon.setEksisterendePerson(isNotBlank(relasjon.getRelatertPerson()));

        if (isBlank(relasjon.getRelatertPerson())) {

            if (isNull(relasjon.getNyRelatertPerson())) {
                relasjon.setNyRelatertPerson(new PersonRequestDTO());
            }
            if (isNull(relasjon.getNyRelatertPerson().getAlder()) &&
                    isNull(relasjon.getNyRelatertPerson().getFoedtEtter()) &&
                    isNull(relasjon.getNyRelatertPerson().getFoedtFoer())) {

                relasjon.getNyRelatertPerson().setFoedtFoer(LocalDateTime.now().minusYears(
                        relasjon.getRelatertPersonsRolle() == Rolle.BARN ? 0 : 70));
                relasjon.getNyRelatertPerson().setFoedtEtter(LocalDateTime.now().minusYears(
                        relasjon.getRelatertPersonsRolle() == Rolle.BARN ? 18 : 90));
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
                relatertPerson.getBostedsadresse().set(0, fellesAdresse);
            }

            relasjon.setRelatertPerson(relatertPerson.getIdent());
        }

        relasjon.setBorIkkeSammen(null);
        relasjon.setNyRelatertPerson(null);
        return relasjon.getRelatertPerson();
    }

    private void getRolle(ForelderBarnRelasjonDTO relasjon, PersonDTO person) {

        if (Rolle.FORELDER == relasjon.getMinRolleForPerson()) {
            relasjon.setMinRolleForPerson(KjoennFraIdentUtility.getKjoenn(person.getIdent()) == MANN ? Rolle.FAR : Rolle.MOR);

        } else if (Rolle.FORELDER == relasjon.getRelatertPersonsRolle()) {
            relasjon.setRelatertPersonsRolle(
                    KjoennFraIdentUtility.getKjoenn(relasjon.getRelatertPerson()) == KVINNE ? Rolle.MOR : Rolle.FAR);
        }
    }

    private LocalDateTime getMaxDato(LocalDateTime dato1, LocalDateTime dato2) {

        return dato1.isAfter(dato2) ? dato1 : dato2;
    }

    private LocalDateTime getLastFlyttedato(PersonDTO person) {

        return person.getBostedsadresse().stream()
                .map(BostedadresseDTO::getGyldigFraOgMed)
                .findFirst()
                .orElse(person.getFoedsel().stream()
                        .map(FoedselDTO::getFoedselsdato)
                        .findFirst()
                        .orElse(DatoFraIdentUtility.getDato(person.getIdent()).atStartOfDay()));
    }

    private void createMotsattRelasjon(ForelderBarnRelasjonDTO relasjon, String hovedperson) {

        DbPerson relatertPerson = personRepository.findByIdent(relasjon.getRelatertPerson()).get();
        ForelderBarnRelasjonDTO relatertFamilierelasjon = mapperFacade.map(relasjon, ForelderBarnRelasjonDTO.class);
        relatertFamilierelasjon.setRelatertPerson(hovedperson);
        swapRoller(relatertFamilierelasjon);
        relatertFamilierelasjon.setId(relatertPerson.getPerson().getForelderBarnRelasjon().stream().findFirst()
                .map(ForelderBarnRelasjonDTO::getId)
                .orElse(0) + 1);
        relatertPerson.getPerson().getForelderBarnRelasjon().add(0, relatertFamilierelasjon);
        personRepository.save(relatertPerson);
    }

    private KjoennDTO.Kjoenn getKjoenn(Rolle rolle) {

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

        Rolle rolle = relasjon.getMinRolleForPerson();
        relasjon.setMinRolleForPerson(relasjon.getRelatertPersonsRolle());
        relasjon.setRelatertPersonsRolle(rolle);
        return relasjon;
    }
}
