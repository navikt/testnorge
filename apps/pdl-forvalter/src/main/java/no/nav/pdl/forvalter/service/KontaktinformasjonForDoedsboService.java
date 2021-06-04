package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.model.RelasjonType;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlKontaktinformasjonForDoedsbo;
import no.nav.pdl.forvalter.domain.PdlKontaktinformasjonForDoedsbo.Adressat;
import no.nav.pdl.forvalter.domain.PdlKontaktinformasjonForDoedsbo.PdlKontaktpersonMedIdNummer;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.pdl.forvalter.dto.RsNavn;
import no.nav.pdl.forvalter.dto.RsPersonRequest;
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

    private static final String VALIDATION_SKIFTEREFORM_MISSING = "KontaktinformasjonForDødsbo: Skiftereform må angis";
    private static final String VALIDATION_ADRESSAT_MISSING = "KontaktinformasjonForDødsbo: addressat må oppgis";
    private static final String VALIDATION_ADRESSAT_AMBIGOUS = "KontaktinformasjonForDødsbo: kun av addressat skal oppgis; " +
            "advokatSomAdressat, kontaktpersonMedIdNummerSomAdressat, kontaktpersonUtenIdNummerSomAdressat eller " +
            "organisasjonSomAdressat";
    private static final String VALIDATION_IDNUMBER_INVALID = "KontaktinformasjonForDødsbo: adressat med idnummer %s " +
            "ikke funnet i database";
    private static final String VALIDATION_FOEDSELSDATO_MISSING = "KontaktinformasjonForDødsbo: adressat uten idnummer behøver " +
            "fødselsdato";
    private static final String VALIDATION_NAVN_INVALID = "KontaktinformasjonForDødsbo: adressat har ugyldig personnavn";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final MapperFacade mapperFacade;
    private final AdresseServiceConsumer adresseServiceConsumer;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    protected static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    public List<PdlKontaktinformasjonForDoedsbo> convert(PdlPerson person) {

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

    private void validate(PdlKontaktinformasjonForDoedsbo kontaktinfo) {

        if (isNull(kontaktinfo.getSkifteform())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_SKIFTEREFORM_MISSING);
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
                    !personRepository.existsByIdent(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat().getIdnummer())) {
                throw new HttpClientErrorException(BAD_REQUEST, format(VALIDATION_IDNUMBER_INVALID,
                        kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat().getIdnummer()));
            }
            if (nonNull(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat()) &&
                    isNull(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getFoedselsdato())) {
                throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_FOEDSELSDATO_MISSING);
            }
            if (!isValidPersonnavn(kontaktinfo.getAdressat())) {
                throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_NAVN_INVALID);
            }
        }
    }

    private void handle(PdlKontaktinformasjonForDoedsbo kontaktinfo, PdlPerson person) {

        if (isNull(kontaktinfo.getUtstedtDato())) {
            kontaktinfo.setUtstedtDato(LocalDateTime.now());
        }

        if (isBlank(kontaktinfo.getAdresselinje1())) {

            var bostedadresse = person.getBostedsadresse().stream().findFirst();
            if (bostedadresse.isPresent()) {
                mapperFacade.map(bostedadresse.get(), kontaktinfo);

            } else {
                mapperFacade.map(adresseServiceConsumer.getAdresse(PdlVegadresse.builder()
                        .postnummer(kontaktinfo.getPostnummer())
                        .build(), null), kontaktinfo);
            }
        }

        if (nonNull(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat()) &&
                isBlank(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat().getIdnummer())) {

            leggTilNyAddressat(kontaktinfo.getAdressat().getKontaktpersonMedIdNummerSomAdressat(), person);
        }
        if (nonNull(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat())) {

            leggTilNavn(kontaktinfo.getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getNavn());
        }
        if (nonNull(kontaktinfo.getAdressat().getAdvokatSomAdressat())) {

            leggTilNavn(kontaktinfo.getAdressat().getAdvokatSomAdressat().getKontaktperson());
        }
        if (nonNull(kontaktinfo.getAdressat().getOrganisasjonSomAdressat())) {

            leggTilNavn(kontaktinfo.getAdressat().getOrganisasjonSomAdressat().getKontaktperson());
        }

    }

    private void leggTilNavn(RsNavn navn) {

        if (isNull(navn)) {
            navn = new RsNavn();
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
    }

    private boolean isValidPersonnavn(Adressat adressat) {

        var utenIdNummer = isNull(adressat.getKontaktpersonUtenIdNummerSomAdressat()) ||
                nonNull(adressat.getKontaktpersonUtenIdNummerSomAdressat().getNavn()) &&
                isValid(adressat.getKontaktpersonUtenIdNummerSomAdressat().getNavn());

        var advokat = isNull(adressat.getAdvokatSomAdressat()) ||
                nonNull(adressat.getAdvokatSomAdressat().getKontaktperson()) &&
                isValid(adressat.getAdvokatSomAdressat().getKontaktperson());

        var organisasjon = isNull(adressat.getOrganisasjonSomAdressat()) ||
                nonNull(adressat.getOrganisasjonSomAdressat().getKontaktperson()) &&
                isValid(adressat.getOrganisasjonSomAdressat().getKontaktperson());

        return utenIdNummer && advokat && organisasjon;
    }

    private boolean isValid(RsNavn navn) {

        return (isNotBlank(navn.getFornavn()) ||
                isNotBlank(navn.getMellomnavn()) ||
                isNotBlank(navn.getEtternavn())) &&
                genererNavnServiceConsumer.verifyNavn(NavnDTO.builder()
                        .adjektiv(navn.getFornavn())
                        .adverb(navn.getMellomnavn())
                        .substantiv(navn.getEtternavn())
                        .build());
    }

    private void leggTilNyAddressat(PdlKontaktpersonMedIdNummer adressat, PdlPerson person) {

        if (isNull(adressat.getNyKontaktPerson())) {
            adressat.setNyKontaktPerson(new RsPersonRequest());
        }

        if (isNull(adressat.getNyKontaktPerson().getAlder()) &&
                isNull(adressat.getNyKontaktPerson().getFoedtEtter()) &&
                isNull(adressat.getNyKontaktPerson().getFoedtFoer())) {

            adressat.getNyKontaktPerson().setFoedtFoer(LocalDateTime.now().minusYears(18));
            adressat.getNyKontaktPerson().setFoedtEtter(LocalDateTime.now().minusYears(67));
        }

        adressat.setIdnummer(
                createPersonService.execute(adressat.getNyKontaktPerson()));
        relasjonService.setRelasjoner(person.getIdent(), RelasjonType.AVDOEDD_SIN_ADRESSAT,
                adressat.getIdnummer(), RelasjonType.ADRESSAT_FOR_DOEDSBO);
        adressat.setNyKontaktPerson(null);
    }
}