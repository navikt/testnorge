package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.OrganisasjonForvalterConsumer;
import no.nav.pdl.forvalter.database.model.RelasjonType;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.KontaktinformasjonForDoedsboDTO;
import no.nav.pdl.forvalter.domain.KontaktinformasjonForDoedsboDTO.AdressatDTO;
import no.nav.pdl.forvalter.domain.KontaktinformasjonForDoedsboDTO.KontaktpersonMedIdNummerDTO;
import no.nav.pdl.forvalter.domain.KontaktinformasjonForDoedsboDTO.OrganisasjonDTO;
import no.nav.pdl.forvalter.domain.KontaktinformasjonForDoedsboDTO.PersonNavnDTO;
import no.nav.pdl.forvalter.domain.PersonDTO;
import no.nav.pdl.forvalter.domain.PersonRequestDTO;
import no.nav.pdl.forvalter.domain.VegadresseDTO;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class KontaktinformasjonForDoedsboService {

    private static final String VALIDATION_SKIFTEFORM_MISSING = "KontaktinformasjonForDoedsbo: Skifteform må angis";
    private static final String VALIDATION_ADRESSAT_MISSING = "KontaktinformasjonForDoedsbo: addressat må oppgis";
    private static final String VALIDATION_ADRESSAT_AMBIGOUS = "KontaktinformasjonForDoedsbo: kun en av disse adressater skal oppgis: " +
            "advokatSomAdressat, kontaktpersonMedIdNummerSomAdressat, kontaktpersonUtenIdNummerSomAdressat eller " +
            "organisasjonSomAdressat";
    private static final String VALIDATION_IDNUMBER_INVALID = "KontaktinformasjonForDoedsbo: adressat med idnummer %s " +
            "ikke funnet i database";
    private static final String VALIDATION_FOEDSELSDATO_MISSING = "KontaktinformasjonForDoedsbo: adressat uten idnummer behøver " +
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

    protected static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    public List<KontaktinformasjonForDoedsboDTO> convert(PersonDTO person) {

        for (var type : person.getKontaktinformasjonForDoedsbo()) {

            if (type.isNew()) {
                validate(type);

                handle(type, person);
                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getKontaktinformasjonForDoedsbo();
    }

    private void validate(KontaktinformasjonForDoedsboDTO kontaktinfo) {

        if (isNull(kontaktinfo.getSkifteform())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_SKIFTEFORM_MISSING);
        }

        if (isNull(kontaktinfo.getAdressat())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_ADRESSAT_MISSING);

        } else {
            if (count(kontaktinfo.getAdressat().getAdvokatSomAdressat()) +
                    count(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat()) +
                    count(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat()) +
                    count(kontaktinfo.getAdressat().getOrganisasjonSomAdressat()) > 1) {
                throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_ADRESSAT_AMBIGOUS);
            }
            if (nonNull(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat()) &&
                    isNotBlank(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat().getIdnummer()) &&
                    !personRepository.existsByIdent(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat().getIdnummer())) {
                throw new HttpClientErrorException(BAD_REQUEST, format(VALIDATION_IDNUMBER_INVALID,
                        kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat().getIdnummer()));
            }
            if (nonNull(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat()) &&
                    isNull(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getFoedselsdato())) {
                throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_FOEDSELSDATO_MISSING);
            }
            if (!isValidPersonnavn(kontaktinfo.getAdressat())) {
                throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_PERSONNAVN_INVALID);
            }
            if (!isValidOrganisasjonName(kontaktinfo.getAdressat())) {
                throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_ORGANISASJON_NAVN_INVALID);
            }
            if (nonNull(kontaktinfo.getAdressat().getAdvokatSomAdressat())
                    && !isValidOrgnummerOgNavn(kontaktinfo.getAdressat().getAdvokatSomAdressat()) ||
                    nonNull(kontaktinfo.getAdressat().getOrganisasjonSomAdressat()) &&
                            !isValidOrgnummerOgNavn(kontaktinfo.getAdressat().getOrganisasjonSomAdressat())) {
                throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_ORGANISASJON_NUMMER_OR_NAME_INVALID);
            }
        }
    }

    private void handle(KontaktinformasjonForDoedsboDTO kontaktinfo, PersonDTO person) {

        if (isNull(kontaktinfo.getUtstedtDato())) {
            kontaktinfo.setUtstedtDato(LocalDateTime.now());
        }

        if (isBlank(kontaktinfo.getAdresselinje1())) {

            var bostedadresse = person.getBostedsadresse().stream().findFirst();
            if (bostedadresse.isPresent()) {
                mapperFacade.map(bostedadresse.get(), kontaktinfo);

            } else {
                mapperFacade.map(adresseServiceConsumer.getAdresse(VegadresseDTO.builder()
                        .postnummer(kontaktinfo.getPostnummer())
                        .build(), null), kontaktinfo);
            }
        }

        if (nonNull(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat()) &&
                isBlank(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat().getIdnummer())) {

            leggTilNyAddressat(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat(), person);
        }
        if (nonNull(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat())) {

            kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat().setNavn(
                    leggTilPersonnavn(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getNavn()));
            leggTilPersonnavn(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getNavn());
        }
        if (nonNull(kontaktinfo.getAdressat().getAdvokatSomAdressat())) {

            kontaktinfo.getAdressat().getAdvokatSomAdressat().setKontaktperson(
                    leggTilPersonnavn(kontaktinfo.getAdressat().getAdvokatSomAdressat().getKontaktperson()));
            leggTilOrganisasjonsnavn(kontaktinfo.getAdressat().getAdvokatSomAdressat());
        }
        if (nonNull(kontaktinfo.getAdressat().getOrganisasjonSomAdressat())) {

            kontaktinfo.getAdressat().getOrganisasjonSomAdressat().setKontaktperson(
                    leggTilPersonnavn(kontaktinfo.getAdressat().getOrganisasjonSomAdressat().getKontaktperson()));
            leggTilOrganisasjonsnavn(kontaktinfo.getAdressat().getOrganisasjonSomAdressat());
        }
    }

    private void leggTilOrganisasjonsnavn(OrganisasjonDTO pdlOrganisasjon) {

        if (isBlank(pdlOrganisasjon.getOrganisasjonsnavn())) {
            var organisasjon = organisasjonForvalterConsumer.get(pdlOrganisasjon.getOrganisasjonsnummer());
            pdlOrganisasjon.setOrganisasjonsnavn(organisasjon.entrySet().stream()
                    .filter(entry -> "q1".equals(entry.getKey()) || "q2".equals(entry.getKey()))
                    .map(entry -> entry.getValue().get("organisasjonsnavn"))
                    .findFirst().get());
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

    private boolean isValidPersonnavn(AdressatDTO adressatDTO) {

        var utenIdNummer = isNull(adressatDTO.getKontaktpersonUtenIdNummerSomAdressat()) ||
                isNull(adressatDTO.getKontaktpersonUtenIdNummerSomAdressat().getNavn()) ||
                isValid(adressatDTO.getKontaktpersonUtenIdNummerSomAdressat().getNavn());

        var advokat = isNull(adressatDTO.getAdvokatSomAdressat()) ||
                isNull(adressatDTO.getAdvokatSomAdressat().getKontaktperson()) ||
                isValid(adressatDTO.getAdvokatSomAdressat().getKontaktperson());

        var organisasjon = isNull(adressatDTO.getOrganisasjonSomAdressat()) ||
                isNull(adressatDTO.getOrganisasjonSomAdressat().getKontaktperson()) ||
                isValid(adressatDTO.getOrganisasjonSomAdressat().getKontaktperson());

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

    private boolean isValidOrganisasjonName(AdressatDTO adressatDTO) {

        var advokat = isNull(adressatDTO.getAdvokatSomAdressat()) ||
                isNotBlank(adressatDTO.getAdvokatSomAdressat().getOrganisasjonsnummer()) ||
                isBlank(adressatDTO.getAdvokatSomAdressat().getOrganisasjonsnummer()) &&
                        isBlank(adressatDTO.getAdvokatSomAdressat().getOrganisasjonsnavn());

        var organisasjon = isNull(adressatDTO.getOrganisasjonSomAdressat()) ||
                isNotBlank(adressatDTO.getOrganisasjonSomAdressat().getOrganisasjonsnummer()) ||
                isBlank(adressatDTO.getOrganisasjonSomAdressat().getOrganisasjonsnummer()) &&
                        isBlank(adressatDTO.getOrganisasjonSomAdressat().getOrganisasjonsnavn());

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
                pdlOrganisasjon.getOrganisasjonsnavn().equals(organisasjoner.values().stream()
                        .anyMatch(organisasjon -> pdlOrganisasjon.getOrganisasjonsnavn().equals(organisasjon.get("organisasjonsnavn"))));
    }

    private void leggTilNyAddressat(KontaktpersonMedIdNummerDTO adressat, PersonDTO person) {

        if (isNull(adressat.getNyKontaktPerson())) {
            adressat.setNyKontaktPerson(new PersonRequestDTO());
        }

        if (isNull(adressat.getNyKontaktPerson().getAlder()) &&
                isNull(adressat.getNyKontaktPerson().getFoedtEtter()) &&
                isNull(adressat.getNyKontaktPerson().getFoedtFoer())) {

            adressat.getNyKontaktPerson().setFoedtFoer(LocalDateTime.now().minusYears(18));
            adressat.getNyKontaktPerson().setFoedtEtter(LocalDateTime.now().minusYears(67));
        }

        adressat.setIdnummer(
                createPersonService.execute(adressat.getNyKontaktPerson()).getIdent());
        relasjonService.setRelasjoner(person.getIdent(), RelasjonType.AVDOEDD_SIN_ADRESSAT,
                adressat.getIdnummer(), RelasjonType.ADRESSAT_FOR_DOEDSBO);
        adressat.setNyKontaktPerson(null);
    }
}