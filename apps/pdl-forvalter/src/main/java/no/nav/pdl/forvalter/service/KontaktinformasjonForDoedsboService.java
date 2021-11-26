package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.OrganisasjonForvalterConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktinformasjonForDoedsboAdresse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktpersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.OrganisasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PersonNavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    private static final String VALIDATION_AMBIGUOUS_PERSON = "KontaktinformasjonForDoedsbo: personSomKontakt må " +
            "angi kun én av identifikasjonsnummer eller fødselsdato";
    private static final String VALIDATION_PERSONNAVN_INVALID = "KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn";
    private static final String VALIDATION_ORGANISASJON_NAVN_INVALID = "KontaktinformasjonForDoedsbo: organisajonsnavn kan " +
            "ikke oppgis uten at organisasjonsnummer finnes";
    private static final String VALIDATION_ORGANISASJON_NUMMER_OR_NAME_INVALID = "KontaktinformasjonForDoedsbo: organisajonsnummer er tomt " +
            "og/eller angitt organisasjonsnummer/navn finnes ikke i miljø [q1|q2]";

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

                handle(type, person.getIdent());
                type.setKilde(StringUtils.isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : DbVersjonDTO.Master.FREG);
                type.setGjeldende(nonNull(type.getGjeldende()) ? type.getGjeldende(): true);
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
                nonNull(kontaktinfo.getPersonSomKontakt().getFoedselsdato()) &&
                isNotBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())) {

            throw new InvalidRequestException(VALIDATION_AMBIGUOUS_PERSON);
        }

        if (!isValidPersonnavn(kontaktinfo)) {
            throw new InvalidRequestException(VALIDATION_PERSONNAVN_INVALID);
        }

        if (!isValidOrganisasjonName(kontaktinfo)) {
            throw new InvalidRequestException(VALIDATION_ORGANISASJON_NAVN_INVALID);
        }

        if (nonNull(kontaktinfo.getAdvokatSomKontakt())
                && !isValidOrganisasjonIMiljoe(kontaktinfo.getAdvokatSomKontakt()) ||
                nonNull(kontaktinfo.getOrganisasjonSomKontakt()) &&
                        !isValidOrganisasjonIMiljoe(kontaktinfo.getOrganisasjonSomKontakt())) {
            throw new InvalidRequestException(VALIDATION_ORGANISASJON_NUMMER_OR_NAME_INVALID);
        }
    }

    private void handle(KontaktinformasjonForDoedsboDTO kontaktinfo, String hovedperson) {

        if (isNull(kontaktinfo.getAttestutstedelsesdato())) {
            kontaktinfo.setAttestutstedelsesdato(LocalDateTime.now());
        }

        if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
                nonNull(kontaktinfo.getPersonSomKontakt().getFoedselsdato())) {

            Stream.of(
                            leggTilPersonnavn(kontaktinfo.getPersonSomKontakt()),
                            setAdresse(kontaktinfo))
                    .reduce(Flux.empty(), Flux::merge)
                    .collectList()
                    .block();

        } else if (nonNull(kontaktinfo.getPersonSomKontakt())) {
                if (isBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())) {

                    leggTilNyAddressat(kontaktinfo.getPersonSomKontakt(), hovedperson);
                    kontaktinfo.setAdresse(mapperFacade.map(
                            personRepository.findByIdent(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer()).get()
                                    .getPerson().getBostedsadresse().stream().findFirst().get(),
                            KontaktinformasjonForDoedsboAdresse.class));
                    kontaktinfo.getPersonSomKontakt().setNavn(null);

                } else {
                    kontaktinfo.getPersonSomKontakt().setIsIdentExternal(true);
                }

        } else if (nonNull(kontaktinfo.getAdvokatSomKontakt())) {

            Stream.of(
                            setOrganisasjonsnavnOgAdresse(kontaktinfo, kontaktinfo.getAdvokatSomKontakt()),
                            leggTilPersonnavn(kontaktinfo.getAdvokatSomKontakt()))
                    .reduce(Flux.empty(), Flux::merge)
                    .collectList()
                    .block();

        } else if (nonNull(kontaktinfo.getOrganisasjonSomKontakt())) {

            Stream.of(
                            setOrganisasjonsnavnOgAdresse(kontaktinfo, kontaktinfo.getOrganisasjonSomKontakt()),
                            leggTilPersonnavn(kontaktinfo.getOrganisasjonSomKontakt()))
                    .reduce(Flux.empty(), Flux::merge)
                    .collectList()
                    .block();
        }

        if (isNull(kontaktinfo.getAdresse()) || isBlank(kontaktinfo.getAdresse().getAdresselinje1())) {

            setAdresse(kontaktinfo);
        }
    }

    private Flux<Void> setAdresse(KontaktinformasjonForDoedsboDTO kontaktinfo) {

        kontaktinfo.setAdresse(getAdresse(kontaktinfo));
        return Flux.empty();
    }

    private KontaktinformasjonForDoedsboAdresse getAdresse(KontaktinformasjonForDoedsboDTO kontaktinfo) {

        if (nonNull(kontaktinfo.getAdresse()) && isNotBlank(kontaktinfo.getAdresse().getAdresselinje1())) {
            return kontaktinfo.getAdresse();

        } else {
            return mapperFacade.map(adresseServiceConsumer.getVegadresse(VegadresseDTO.builder()
                    .postnummer(nonNull(kontaktinfo.getAdresse()) ? kontaktinfo.getAdresse().getPostnummer() : null)
                    .build(), null), KontaktinformasjonForDoedsboAdresse.class);
        }
    }

    private Flux<Void> setOrganisasjonsnavnOgAdresse(KontaktinformasjonForDoedsboDTO kontaktinfo, OrganisasjonDTO
            organisasjonDto) {

        var organisasjon = organisasjonForvalterConsumer.get(organisasjonDto.getOrganisasjonsnummer())
                .entrySet().stream()
                .filter(entry -> "q1".equals(entry.getKey()) || "q2".equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst().get();

        organisasjonDto.setOrganisasjonsnavn((String) organisasjon.get("organisasjonsnavn"));
        kontaktinfo.setAdresse(!((List<Map>) organisasjon.get("adresser")).isEmpty() ?
                mapperFacade.map(((List<Map>) organisasjon.get("adresser")).get(0), KontaktinformasjonForDoedsboAdresse.class) :
                null);

        return Flux.empty();
    }

    private Flux<Void> leggTilPersonnavn(KontaktpersonDTO kontaktpersonDTO) {

        kontaktpersonDTO.setNavn(leggTilPersonnavn(kontaktpersonDTO.getNavn()));
        return Flux.empty();
    }

    private Flux<Void> leggTilPersonnavn(OrganisasjonDTO organisasjon) {

        organisasjon.setKontaktperson(leggTilPersonnavn(organisasjon.getKontaktperson()));
        return Flux.empty();
    }

    private PersonNavnDTO leggTilPersonnavn(PersonNavnDTO personnavn) {

        var navn = isNull(personnavn) ? new PersonNavnDTO() : personnavn;
        if (isBlank(navn.getFornavn()) || isBlank(navn.getEtternavn()) ||
                (isBlank(navn.getMellomnavn()) && isTrue(navn.getHasMellomnavn()))) {

            var nyttNavn = genererNavnServiceConsumer.getNavn(1);
            if (nyttNavn.isPresent()) {
                navn.setFornavn(blankCheck(navn.getFornavn(), nyttNavn.get().getAdjektiv()));
                navn.setEtternavn(blankCheck(navn.getEtternavn(), nyttNavn.get().getSubstantiv()));
                navn.setMellomnavn(blankCheck(navn.getMellomnavn(),
                        isTrue(navn.getHasMellomnavn()) ? nyttNavn.get().getAdverb() : null));
            }
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

    private boolean isValidOrganisasjonIMiljoe(OrganisasjonDTO pdlOrganisasjon) {

        if (isBlank(pdlOrganisasjon.getOrganisasjonsnummer())) {
            return false;
        }
        var organisasjoner = organisasjonForvalterConsumer.get(pdlOrganisasjon.getOrganisasjonsnummer());
        if (organisasjoner.isEmpty() || !organisasjoner.containsKey("q1") && !organisasjoner.containsKey("q2")) {
            return false;
        }
        return isBlank(pdlOrganisasjon.getOrganisasjonsnavn()) ||
                organisasjoner.values().stream()
                        .anyMatch(organisasjon -> pdlOrganisasjon.getOrganisasjonsnavn()
                                .equalsIgnoreCase((String) organisasjon.get("organisasjonsnavn")));
    }

    private void leggTilNyAddressat(KontaktpersonDTO kontakt, String hovedperson) {

        if (isNull(kontakt.getNyKontaktperson())) {
            kontakt.setNyKontaktperson(new PersonRequestDTO());
        }

        if (isNull(kontakt.getNyKontaktperson().getAlder()) &&
                isNull(kontakt.getNyKontaktperson().getFoedtEtter()) &&
                isNull(kontakt.getNyKontaktperson().getFoedtFoer())) {

            kontakt.getNyKontaktperson().setFoedtFoer(LocalDateTime.now().minusYears(18));
            kontakt.getNyKontaktperson().setFoedtEtter(LocalDateTime.now().minusYears(75));
        }

        kontakt.setIdentifikasjonsnummer(
                createPersonService.execute(kontakt.getNyKontaktperson()).getIdent());
        relasjonService.setRelasjoner(hovedperson, RelasjonType.AVDOEDD_FOR_KONTAKT,
                kontakt.getIdentifikasjonsnummer(), RelasjonType.KONTAKT_FOR_DOEDSBO);
    }
}