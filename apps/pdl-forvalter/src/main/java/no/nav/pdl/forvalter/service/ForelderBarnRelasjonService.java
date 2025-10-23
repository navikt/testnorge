package no.nav.pdl.forvalter.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.PersonUtenIdentifikatorRequest;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.pdl.forvalter.utils.KjoennUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand.defaultAdresse;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ForelderBarnRelasjonService implements BiValidation<ForelderBarnRelasjonDTO, PersonDTO> {

    private static final String INVALID_PERSON_ID_EXCEPTION = "ForelderBarnRelasjon: Relatert person skal finnes med eller uten ident, " +
            "ikke begge deler";
    private static final String INVALID_EMPTY_MIN_ROLLE_EXCEPTION = "ForelderBarnRelasjon: min rolle for person må oppgis";
    private static final String INVALID_EMPTY_RELATERT_PERSON_ROLLE_EXCEPTION = "ForelderBarnRelasjon: relatert persons rolle må oppgis";
    private static final String AMBIGUOUS_PERSON_ROLLE_EXCEPTION = "ForelderBarnRelasjon: min rolle og relatert persons " +
            "rolle må være av type barn -- forelder, eller forelder -- barn";
    private static final String INVALID_RELATERT_PERSON_EXCEPTION = "ForelderBarnRelasjon: Relatert person %s finnes ikke";
    private static final String INVALID_AMBIGUOUS_ADRESSE = "Delt bosted: kun én adresse skal være satt (vegadresse, " +
            "ukjentBosted, matrikkeladresse)";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final CreatePersonUtenIdentifikatorService createPersonUtenIdentifikatorService;
    private final RelasjonService relasjonService;
    private final MapperFacade mapperFacade;
    private final DeltBostedService deltBostedService;

    public List<ForelderBarnRelasjonDTO> convert(PersonDTO person) {

        var nyeRelasjoner = new ArrayList<ForelderBarnRelasjonDTO>();
        for (var type : person.getForelderBarnRelasjon()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
                nyeRelasjoner.addAll(handle(type, person));
            }
        }
        nyeRelasjoner
                .forEach(relasjon -> person.getForelderBarnRelasjon().addFirst(relasjon));
        return person.getForelderBarnRelasjon();
    }

    @Override
    public void validate(ForelderBarnRelasjonDTO relasjon, PersonDTO person) {

        if (nonNull(relasjon.getRelatertPersonUtenFolkeregisteridentifikator()) &&
                (nonNull(relasjon.getRelatertPerson()) || nonNull(relasjon.getNyRelatertPerson()))) {
            throw new InvalidRequestException(INVALID_PERSON_ID_EXCEPTION);
        }

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

        if (!isTestnorgeIdent(person.getIdent()) && isNotBlank(relasjon.getRelatertPerson()) &&
                !personRepository.existsByIdent(relasjon.getRelatertPerson())) {

            throw new InvalidRequestException(String.format(INVALID_RELATERT_PERSON_EXCEPTION,
                    relasjon.getRelatertPerson()));
        }

        if (nonNull(relasjon.getDeltBosted()) && relasjon.getDeltBosted().countAdresser() > 1) {

            throw new InvalidRequestException(INVALID_AMBIGUOUS_ADRESSE);
        }
    }

    private List<ForelderBarnRelasjonDTO> handle(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        var request = mapperFacade.map(relasjon, ForelderBarnRelasjonDTO.class);
        setRelatertPerson(relasjon, hovedperson);
        addForelderBarnRelasjon(relasjon, hovedperson);

        if (isNotBlank(request.getRelatertPerson())) {
            return emptyList();
        }

        if (request.getRelatertPersonsRolle() == Rolle.BARN &&
                isNotTrue(request.getPartnerErIkkeForelder()) && hovedperson.getSivilstand().stream()
                .anyMatch(sivilstand -> isNotBlank(sivilstand.getRelatertVedSivilstand()))) {

            request.setRelatertPerson(relasjon.getRelatertPerson());
            request.setRelatertPersonUtenFolkeregisteridentifikator(relasjon.getRelatertPersonUtenFolkeregisteridentifikator());
            request.setNyRelatertPerson(null);
            request.setBorIkkeSammen(null);
            request.setMinRolleForPerson(switch (request.getMinRolleForPerson()) {
                case FAR, MEDMOR -> Rolle.MOR;
                case MOR -> Rolle.FAR;
                default -> request.getMinRolleForPerson();
            });

            var partnere = hovedperson.getSivilstand().stream()
                    .filter(sivilstand -> isNotBlank(sivilstand.getRelatertVedSivilstand()))
                    .map(SivilstandDato::new)
                    .sorted(Comparator.comparing(SivilstandDato::getSivilstandsdato).reversed())
                    .toList();

            personRepository.findByIdent(relasjon.getRelatertPerson())
                    .map(relatertPerson -> FoedselsdatoUtility.getFoedselsdato(relatertPerson.getPerson()))
                    .map(foedselsdato -> getPartnerIdent(partnere, foedselsdato))
                    .flatMap(personRepository::findByIdent)
                    .ifPresent(partnerPerson -> partnerPerson.getPerson().getForelderBarnRelasjon()
                            .addFirst(addForelderBarnRelasjon(request, partnerPerson.getPerson())));
        }

        if (request.getRelatertPersonsRolle() == Rolle.BARN && nonNull(relasjon.getDeltBosted())) {
            deltBostedService.handle(relasjon.getDeltBosted(), hovedperson, relasjon.getRelatertPerson());
        }

        relasjon.setPartnerErIkkeForelder(null);

        if (request.getMinRolleForPerson() == Rolle.BARN && request.getRelatertPersonsRolle() == Rolle.FORELDER) {

            var forelderRelasjon = mapperFacade.map(request, ForelderBarnRelasjonDTO.class);
            personRepository.findByIdent(relasjon.getRelatertPerson())
                    .map(DbPerson::getPerson)
                    .ifPresent(person -> forelderRelasjon.setNyRelatertPerson(PersonRequestDTO.builder()
                            .kjoenn(KjoennFraIdentUtility.getKjoenn(person) == MANN ? KVINNE : MANN)
                            .build()));
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

    private static String getPartnerIdent(List<SivilstandDato> partnere, LocalDateTime relatertPersonDato) {

        for (var partnersivilstand : partnere) {
            if (partnersivilstand.getSivilstandsdato().isBefore(relatertPersonDato)) {
                return partnersivilstand.getRelatertVedSivilstand();
            }
        }
        return partnere.getLast().getRelatertVedSivilstand();
    }

    private ForelderBarnRelasjonDTO addForelderBarnRelasjon(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        setRolle(relasjon, hovedperson);
        if (isBlank(relasjon.getRelatertPerson())) {
            return relasjon;
        }
        createMotsattRelasjon(relasjon, hovedperson.getIdent());

        relasjonService.setRelasjoner(hovedperson.getIdent(),
                relasjon.getRelatertPersonsRolle() == Rolle.BARN ? FAMILIERELASJON_FORELDER : FAMILIERELASJON_BARN,
                relasjon.getRelatertPerson(),
                relasjon.getRelatertPersonsRolle() == Rolle.BARN ? FAMILIERELASJON_BARN : FAMILIERELASJON_FORELDER);

        return relasjon;
    }

    private String setRelatertPerson(ForelderBarnRelasjonDTO relasjon, PersonDTO hovedperson) {

        relasjon.setEksisterendePerson(isNotBlank(relasjon.getRelatertPerson()));

        if (nonNull(relasjon.getRelatertPersonUtenFolkeregisteridentifikator())) {

            var request = mapperFacade.map(relasjon.getRelatertPersonUtenFolkeregisteridentifikator(),
                    PersonUtenIdentifikatorRequest.class);

            request.setMinRolle(relasjon.getMinRolleForPerson());
            request.setRelatertStatsborgerskap(hovedperson.getStatsborgerskap().stream()
                    .map(StatsborgerskapDTO::getLandkode)
                    .findFirst()
                    .orElse(null));
            relasjon.setRelatertPersonUtenFolkeregisteridentifikator(
                    createPersonUtenIdentifikatorService.execute(request));

        } else if (isBlank(relasjon.getRelatertPerson())) {

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
            EgenskaperFraHovedperson.kopierData(hovedperson, relasjon.getNyRelatertPerson());

            PersonDTO relatertPerson = createPersonService.execute(relasjon.getNyRelatertPerson());

            if (isNotTrue(relasjon.getBorIkkeSammen()) && !hovedperson.getBostedsadresse().isEmpty()) {
                var fellesAdresse = mapperFacade.map(hovedperson.getBostedsadresse().stream()
                        .findFirst()
                        .orElse(BostedadresseDTO.builder()
                                .vegadresse(mapperFacade.map(defaultAdresse(), VegadresseDTO.class))
                                .build()), BostedadresseDTO.class);
                fellesAdresse.setGyldigFraOgMed(getMaxDato(getLastFlyttedato(hovedperson), getLastFlyttedato(relatertPerson)));
                if (!relatertPerson.getBostedsadresse().isEmpty()) {
                    relatertPerson.getBostedsadresse().set(0, fellesAdresse);
                }
            }

            relasjon.setRelatertPerson(relatertPerson.getIdent());
        }

        relasjon.setBorIkkeSammen(null);
        relasjon.setNyRelatertPerson(null);
        return relasjon.getRelatertPerson();
    }

    private void setRolle(ForelderBarnRelasjonDTO relasjon, PersonDTO person) {

        if (Rolle.FORELDER == relasjon.getMinRolleForPerson()) {
            relasjon.setMinRolleForPerson(KjoennFraIdentUtility.getKjoenn(person) == MANN ? Rolle.FAR : Rolle.MOR);

        } else if (Rolle.FORELDER == relasjon.getRelatertPersonsRolle()) {

            personRepository.findByIdent(relasjon.getRelatertPerson())
                    .map(DbPerson::getPerson)
                    .ifPresent(relatertPerson ->
                            relasjon.setRelatertPersonsRolle(KjoennFraIdentUtility
                                    .getKjoenn(relatertPerson) == MANN ? Rolle.FAR : Rolle.MOR));
        }
    }

    private LocalDateTime getMaxDato(LocalDateTime dato1, LocalDateTime dato2) {

        return dato1.isAfter(dato2) ? dato1 : dato2;
    }

    private LocalDateTime getLastFlyttedato(PersonDTO person) {

        return person.getBostedsadresse().stream()
                .map(BostedadresseDTO::getGyldigFraOgMed)
                .findFirst()
                .orElse(FoedselsdatoUtility.getFoedselsdato(person));
    }

    private void createMotsattRelasjon(ForelderBarnRelasjonDTO relasjon, String hovedperson) {

        var relatertPerson = new AtomicReference<DbPerson>();
        personRepository.findByIdent(relasjon.getRelatertPerson())
                .ifPresentOrElse(relatertPerson::set,
                        () -> relatertPerson.set(personRepository.save(DbPerson.builder()
                                .ident(relasjon.getRelatertPerson())
                                .person(PersonDTO.builder()
                                        .ident(relasjon.getRelatertPerson())
                                        .build())
                                .sistOppdatert(LocalDateTime.now())
                                .build()))
                );
        var relatertFamilierelasjon = mapperFacade.map(relasjon, ForelderBarnRelasjonDTO.class);
        relatertFamilierelasjon.setRelatertPerson(hovedperson);
        swapRoller(relatertFamilierelasjon);
        relatertFamilierelasjon.setId(relatertPerson.get().getPerson().getForelderBarnRelasjon().stream().findFirst()
                .map(ForelderBarnRelasjonDTO::getId)
                .orElse(0) + 1);

        relatertPerson.get().getPerson().getForelderBarnRelasjon().addFirst(relatertFamilierelasjon);
    }

    private KjoennDTO.Kjoenn getKjoenn(Rolle rolle) {

        return switch (rolle) {
            case FAR -> MANN;
            case MOR, MEDMOR -> KVINNE;
            default -> KjoennUtility.getKjoenn();
        };
    }

    private ForelderBarnRelasjonDTO swapRoller(ForelderBarnRelasjonDTO relasjon) {

        Rolle rolle = relasjon.getMinRolleForPerson();
        relasjon.setMinRolleForPerson(relasjon.getRelatertPersonsRolle());
        relasjon.setRelatertPersonsRolle(rolle);
        return relasjon;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SivilstandDato {

        private LocalDateTime sivilstandsdato;
        private String relatertVedSivilstand;

        public SivilstandDato(SivilstandDTO sivilstand) {

            this.sivilstandsdato = getSivilstandDato(sivilstand);
            this.relatertVedSivilstand = sivilstand.getRelatertVedSivilstand();
        }

        private static LocalDateTime getSivilstandDato(SivilstandDTO sivilstand) {

            if (nonNull(sivilstand.getSivilstandsdato())) {
                return sivilstand.getSivilstandsdato();
            } else if (nonNull(sivilstand.getBekreftelsesdato())) {
                return sivilstand.getBekreftelsesdato();
            }
            return LocalDateTime.now();
        }
    }
}
