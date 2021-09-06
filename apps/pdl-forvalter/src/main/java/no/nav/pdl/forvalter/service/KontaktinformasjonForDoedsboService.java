package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.OrganisasjonForvalterConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktpersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.OrganisasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PersonNavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class KontaktinformasjonForDoedsboService implements Validation<KontaktinformasjonForDoedsboDTO> {

    private static final String VALIDATION_SKIFTEFORM_MISSING = "KontaktinformasjonForDoedsbo: Skifteform må angis";
    private static final String VALIDATION_KONTAKT_MISSING = "KontaktinformasjonForDoedsbo: kontakt må oppgis, enten advokatSomKontakt, " +
            "personSomKontakt eller organisasjonSomKontakt";
    private static final String VALIDATION_ADRESSAT_AMBIGOUS = "KontaktinformasjonForDoedsbo: kun en av disse kontakter skal oppgis: " +
            "advokatSomKontakt, personSomKontakt eller organisasjonSomKontakt";
    private static final String VALIDATION_IDNUMBER_INVALID = "KontaktinformasjonForDoedsbo: personSomKontakt med identifikasjonsnummer %s " +
            "ikke funnet i database";
    private static final String VALIDATION_FOEDSELSDATO_MISSING = "KontaktinformasjonForDoedsbo: personSomKontakt uten identifikasjonsnummer behøver " +
            "fødselsdato";
    private static final String VALIDATION_PERSONNAVN_INVALID = "KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn";
    private static final String VALIDATION_ORGANISASJON_NAVN_INVALID = "KontaktinformasjonForDoedsbo: organisajonsnavn kan " +
            "ikke oppgis uten at organisasjonsnummer finnes";
    private static final String VALIDATION_ORGANISASJON_NUMMER_OR_NAME_INVALID = "KontaktinformasjonForDoedsbo: organisajonsnummer er tomt " +
            "og/eller angitt organisasjon finnes ikke i miljø";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final MapperFacade mapperFacade;
    private final AdresseServiceConsumer adresseServiceConsumer;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final OrganisasjonForvalterConsumer organisasjonForvalterConsumer;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    public List<KontaktinformasjonForDoedsboDTO> convert(PersonDTO person) {

        for (var type : person.getKontaktinformasjonForDoedsbo()) {

            if (isTrue(type.getIsNew())) {

                handle(type, person);
                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getKontaktinformasjonForDoedsbo();
    }

    @Override
    public void validate(KontaktinformasjonForDoedsboDTO kontaktinfo) {

        if (isNull(kontaktinfo.getSkifteform())) {
            throw new InvalidRequestException(VALIDATION_SKIFTEFORM_MISSING);
        }

        if (kontaktinfo.countKontakter() == 0) {
            throw new InvalidRequestException(VALIDATION_KONTAKT_MISSING);

        }

        if (kontaktinfo.countKontakter() > 1) {
            throw new InvalidRequestException(VALIDATION_ADRESSAT_AMBIGOUS);
        }

        if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
                isNotBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer()) &&
                !personRepository.existsByIdent(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())) {
            throw new InvalidRequestException(format(VALIDATION_IDNUMBER_INVALID,
                    kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer()));
        }

        if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
                isNull(kontaktinfo.getPersonSomKontakt().getFoedselsdato()) &&
                isNull(kontaktinfo.getPersonSomKontakt().getNyKontaktPerson()) &&
                isBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())) {

            throw new InvalidRequestException(VALIDATION_FOEDSELSDATO_MISSING);
        }

        if (!isValidPersonnavn(kontaktinfo)) {
            throw new InvalidRequestException(VALIDATION_PERSONNAVN_INVALID);
        }

        if (!isValidOrganisasjonName(kontaktinfo)) {
            throw new InvalidRequestException(VALIDATION_ORGANISASJON_NAVN_INVALID);
        }

        if (nonNull(kontaktinfo.getAdvokatSomKontakt())
                && !isValidOrgnummerOgNavn(kontaktinfo.getAdvokatSomKontakt()) ||
                nonNull(kontaktinfo.getOrganisasjonSomKontakt()) &&
                        !isValidOrgnummerOgNavn(kontaktinfo.getOrganisasjonSomKontakt())) {
            throw new InvalidRequestException(VALIDATION_ORGANISASJON_NUMMER_OR_NAME_INVALID);
        }
    }

    private void handle(KontaktinformasjonForDoedsboDTO kontaktinfo, PersonDTO person) {

        if (isNull(kontaktinfo.getAttestutstedelsesdato())) {
            kontaktinfo.setAttestutstedelsesdato(LocalDateTime.now());
        }

        if (isBlank(kontaktinfo.getAdresse().getAdresselinje1())) {

            var bostedadresse = person.getBostedsadresse().stream().findFirst();
            if (bostedadresse.isPresent()) {
                mapperFacade.map(bostedadresse.get(), kontaktinfo.getAdresse());

            } else {
                mapperFacade.map(adresseServiceConsumer.getVegadresse(VegadresseDTO.builder()
                        .postnummer(kontaktinfo.getAdresse().getPostnummer())
                        .build(), null), kontaktinfo.getAdresse());
            }
        }

        if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
                isBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer()) &&
                nonNull(kontaktinfo.getPersonSomKontakt())) {

            leggTilNyAddressat(kontaktinfo.getPersonSomKontakt(), person);

        } else if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
                nonNull(kontaktinfo.getPersonSomKontakt().getFoedselsdato())) {

            kontaktinfo.getPersonSomKontakt().setNavn(
                    leggTilPersonnavn(kontaktinfo.getPersonSomKontakt().getNavn()));

        } else if (nonNull(kontaktinfo.getAdvokatSomKontakt())) {

            kontaktinfo.getAdvokatSomKontakt().setKontaktperson(
                    leggTilPersonnavn(kontaktinfo.getAdvokatSomKontakt().getKontaktperson()));
            leggTilOrganisasjonsnavn(kontaktinfo.getAdvokatSomKontakt());

        } else if (nonNull(kontaktinfo.getOrganisasjonSomKontakt())) {

            kontaktinfo.getOrganisasjonSomKontakt().setKontaktperson(
                    leggTilPersonnavn(kontaktinfo.getOrganisasjonSomKontakt().getKontaktperson()));
            leggTilOrganisasjonsnavn(kontaktinfo.getOrganisasjonSomKontakt());
        }
    }

    private void leggTilOrganisasjonsnavn(OrganisasjonDTO pdlOrganisasjon) {

        if (isBlank(pdlOrganisasjon.getOrganisasjonsnavn())) {
            var organisasjonsnavn = organisasjonForvalterConsumer.get(pdlOrganisasjon.getOrganisasjonsnummer())
                    .entrySet().stream()
                    .filter(entry -> "q1".equals(entry.getKey()) || "q2".equals(entry.getKey()))
                    .map(entry -> entry.getValue().get("organisasjonsnavn"))
                    .findFirst();

            pdlOrganisasjon.setOrganisasjonsnavn(organisasjonsnavn.isPresent() ?
                    organisasjonsnavn.get() : "Organisajonsnavn ikke funnet");
        }
    }

    private PersonNavnDTO leggTilPersonnavn(PersonNavnDTO navn) {

        if (isNull(navn)) {
            navn = new PersonNavnDTO();
        }
        if (isBlank(navn.getFornavn()) || isBlank(navn.getEtternavn()) ||
                (isBlank(navn.getMellomnavn()) && isTrue(navn.getHasMellomnavn()))) {

            var nyttNavn = genererNavnServiceConsumer.getNavn(1);
            if (nyttNavn.isPresent()) {
                navn.setFornavn(blankCheck(navn.getFornavn(), nyttNavn.get().getAdjektiv()));
                navn.setEtternavn(blankCheck(navn.getEtternavn(), nyttNavn.get().getSubstantiv()));
                navn.setMellomnavn(blankCheck(navn.getMellomnavn(),
                        isTrue(navn.getHasMellomnavn()) ? nyttNavn.get().getAdverb() : null));
            }
            navn.setHasMellomnavn(null);
        }
        return navn;
    }

    private boolean isValidPersonnavn(KontaktinformasjonForDoedsboDTO kontakt) {

        var utenIdNummer = isNull(kontakt.getPersonSomKontakt()) ||
                isNull(kontakt.getPersonSomKontakt().getNavn()) ||
                isValid(kontakt.getPersonSomKontakt().getNavn());

        var advokat = isNull(kontakt.getAdvokatSomKontakt()) ||
                isNull(kontakt.getAdvokatSomKontakt().getKontaktperson()) ||
                isValid(kontakt.getAdvokatSomKontakt().getKontaktperson());

        var organisasjon = isNull(kontakt.getOrganisasjonSomKontakt()) ||
                isNull(kontakt.getOrganisasjonSomKontakt().getKontaktperson()) ||
                isValid(kontakt.getOrganisasjonSomKontakt().getKontaktperson());

        return utenIdNummer && advokat && organisasjon;
    }

    private boolean isValid(PersonNavnDTO navn) {

        return isBlank(navn.getFornavn()) &&
                isBlank(navn.getMellomnavn()) &&
                isBlank(navn.getEtternavn()) ||
                genererNavnServiceConsumer.verifyNavn(NavnDTO.builder()
                        .adjektiv(navn.getFornavn())
                        .adverb(navn.getMellomnavn())
                        .substantiv(navn.getEtternavn())
                        .build());
    }

    private boolean isValidOrganisasjonName(KontaktinformasjonForDoedsboDTO kontakt) {

        var advokat = isNull(kontakt.getAdvokatSomKontakt()) ||
                isNotBlank(kontakt.getAdvokatSomKontakt().getOrganisasjonsnummer()) ||
                isBlank(kontakt.getAdvokatSomKontakt().getOrganisasjonsnummer()) &&
                        isBlank(kontakt.getAdvokatSomKontakt().getOrganisasjonsnavn());

        var organisasjon = isNull(kontakt.getOrganisasjonSomKontakt()) ||
                isNotBlank(kontakt.getOrganisasjonSomKontakt().getOrganisasjonsnummer()) ||
                isBlank(kontakt.getOrganisasjonSomKontakt().getOrganisasjonsnummer()) &&
                        isBlank(kontakt.getOrganisasjonSomKontakt().getOrganisasjonsnavn());

        return advokat && organisasjon;
    }

    private boolean isValidOrgnummerOgNavn(OrganisasjonDTO pdlOrganisasjon) {

        if (isBlank(pdlOrganisasjon.getOrganisasjonsnummer())) {
            return false;
        }
        var organisasjoner = organisasjonForvalterConsumer.get(pdlOrganisasjon.getOrganisasjonsnummer());
        if (organisasjoner.isEmpty() || !organisasjoner.containsKey("q1") && organisasjoner.containsKey("q2")) {
            return false;
        }
        return isBlank(pdlOrganisasjon.getOrganisasjonsnavn()) ||
                organisasjoner.values().stream()
                        .anyMatch(organisasjon -> pdlOrganisasjon.getOrganisasjonsnavn().equals(organisasjon.get("organisasjonsnavn")));
    }

    private void leggTilNyAddressat(KontaktpersonDTO kontakt, PersonDTO person) {

        if (isNull(kontakt.getNyKontaktPerson())) {
            kontakt.setNyKontaktPerson(new PersonRequestDTO());
        }

        if (isNull(kontakt.getNyKontaktPerson().getAlder()) &&
                isNull(kontakt.getNyKontaktPerson().getFoedtEtter()) &&
                isNull(kontakt.getNyKontaktPerson().getFoedtFoer())) {

            kontakt.getNyKontaktPerson().setFoedtFoer(LocalDateTime.now().minusYears(18));
            kontakt.getNyKontaktPerson().setFoedtEtter(LocalDateTime.now().minusYears(67));
        }

        if (isNull(kontakt.getNyKontaktPerson().getSyntetisk())) {
            kontakt.getNyKontaktPerson().setSyntetisk(SyntetiskFraIdentUtility.isSyntetisk(person.getIdent()));
        }

        kontakt.setIdentifikasjonsnummer(
                createPersonService.execute(kontakt.getNyKontaktPerson()).getIdent());
        relasjonService.setRelasjoner(person.getIdent(), RelasjonType.AVDOEDD_SIN_ADRESSAT,
                kontakt.getIdentifikasjonsnummer(), RelasjonType.ADRESSAT_FOR_DOEDSBO);
        kontakt.setNyKontaktPerson(null);
    }
}