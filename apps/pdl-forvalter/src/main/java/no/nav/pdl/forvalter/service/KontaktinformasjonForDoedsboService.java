package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.consumer.OrganisasjonForvalterConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.mapper.MappingContextUtils;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktinformasjonForDoedsboAdresse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktpersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.OrganisasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PersonNavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
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
    private final EnkelAdresseService enkelAdresseService;
    private final KodeverkConsumer kodeverkConsumer;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    public Mono<DbPerson> convert(DbPerson dbPerson) {

        return Flux.fromIterable(dbPerson.getPerson().getKontaktinformasjonForDoedsbo())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, dbPerson.getIdent()))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, dbPerson.getPerson()));
                })
                .collectList()
                .thenReturn(dbPerson);
    }

    @Override
    public Mono<Void> validate(KontaktinformasjonForDoedsboDTO kontaktinfo) {

        if (isNull(kontaktinfo.getSkifteform())) {
            return Mono.error(new InvalidRequestException(VALIDATION_SKIFTEFORM_MISSING));
        }

        if (kontaktinfo.countKontakter() == 0) {
            return Mono.error(new InvalidRequestException(VALIDATION_KONTAKT_MISSING));
        }

        if (kontaktinfo.countKontakter() > 1) {
            return Mono.error(new InvalidRequestException(VALIDATION_ADRESSAT_AMBIGOUS));
        }

        if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
            nonNull(kontaktinfo.getPersonSomKontakt().getFoedselsdato()) &&
            isNotBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())) {

            return Mono.error(new InvalidRequestException(VALIDATION_AMBIGUOUS_PERSON));
        }

        if (!isValidOrganisasjonName(kontaktinfo)) {
            return Mono.error(new InvalidRequestException(VALIDATION_ORGANISASJON_NAVN_INVALID));
        }

        return Mono.defer(() -> {
                    if (nonNull(kontaktinfo.getAdvokatSomKontakt())) {
                        return isValidOrganisasjonIMiljoe(kontaktinfo.getAdvokatSomKontakt())
                                .flatMap(valid -> isFalse(valid) ?
                                        Mono.error(new InvalidRequestException(VALIDATION_ORGANISASJON_NUMMER_OR_NAME_INVALID)) :
                                        Mono.empty());
                    }
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
                        isNotBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())) {

                        return personRepository.existsByIdent(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())
                                .flatMap(exists -> isFalse(exists) ?
                                        Mono.error(new InvalidRequestException(format(VALIDATION_IDNUMBER_INVALID,
                                                kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer()))) :
                                        Mono.empty());
                    }
                    return Mono.empty();
                }))
                .then(Mono.defer(() -> {
                    if (nonNull(kontaktinfo.getOrganisasjonSomKontakt())) {
                        return isValidOrganisasjonIMiljoe(kontaktinfo.getOrganisasjonSomKontakt())
                                .flatMap(valid -> isFalse(valid) ?
                                        Mono.error(new InvalidRequestException(VALIDATION_ORGANISASJON_NUMMER_OR_NAME_INVALID)) :
                                        Mono.empty());
                    }
                    return Mono.empty();
                }))
                .then(Mono.defer(() -> {
                    if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
                        isNotBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())) {

                        return personRepository.existsByIdent(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())
                                .flatMap(exists -> isFalse(exists) ?
                                        Mono.error(new InvalidRequestException(format(VALIDATION_IDNUMBER_INVALID,
                                                kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer()))) :
                                        Mono.empty());
                    }
                    return Mono.empty();
                }))
                .then(Mono.defer(() -> isValidPersonnavn(kontaktinfo)))
                .flatMap(valid -> isFalse(valid) ?
                        Mono.error(new InvalidRequestException(VALIDATION_PERSONNAVN_INVALID)) :
                        Mono.empty());
    }

    private Mono<KontaktinformasjonForDoedsboDTO> handle(KontaktinformasjonForDoedsboDTO kontaktinfo, String hovedperson) {

        return kodeverkConsumer.getPoststedNavn()
                .flatMap(poststedsnavn -> {

                    val context = MappingContextUtils.getMappingContext();
                    context.setProperty("poststedsnavn", poststedsnavn);
                    val kontaktinfoOriginal = mapperFacade.map(kontaktinfo, KontaktinformasjonForDoedsboDTO.class);

                    if (isNull(kontaktinfo.getAttestutstedelsesdato())) {
                        kontaktinfo.setAttestutstedelsesdato(LocalDateTime.now());
                    }
                    return Mono.just(kontaktinfoOriginal)
                            .zipWith(Mono.just(poststedsnavn));
                })
                .flatMap(kontaktinfoOriginal -> {

                    Mono<Void> processing = Mono.empty();

                    if (nonNull(kontaktinfo.getPersonSomKontakt()) &&
                        nonNull(kontaktinfo.getPersonSomKontakt().getFoedselsdato())) {

                        processing = leggTilPersonnavn(kontaktinfo.getPersonSomKontakt())
                                .then(getAdresse(kontaktinfo)
                                        .doOnNext(kontaktinfo::setAdresse))
                                .then();

                    } else if (nonNull(kontaktinfo.getAdvokatSomKontakt())) {

                        processing = setOrganisasjonsnavnOgAdresse(kontaktinfo, kontaktinfo.getAdvokatSomKontakt())
                                .then(leggTilPersonnavn(kontaktinfo.getAdvokatSomKontakt()));

                    } else if (nonNull(kontaktinfo.getOrganisasjonSomKontakt())) {

                        processing = setOrganisasjonsnavnOgAdresse(kontaktinfo, kontaktinfo.getOrganisasjonSomKontakt())
                                .then(leggTilPersonnavn(kontaktinfo.getOrganisasjonSomKontakt()));

                    } else if (nonNull(kontaktinfo.getPersonSomKontakt())) {

                        kontaktinfo.getPersonSomKontakt().setEksisterendePerson(
                                isNotBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer()));

                        if (isBlank(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())) {

                            processing = leggTilNyAddressat(kontaktinfo.getPersonSomKontakt(), hovedperson)
                                    .then(Mono.defer(() -> leggTilPersonadresse(kontaktinfo, kontaktinfoOriginal.getT2())))
                                    .then(Mono.fromRunnable(() -> kontaktinfo.getPersonSomKontakt().setNavn(null)));
                        }
                    }

                    if (nonNull(kontaktinfoOriginal.getT1().getAdresse()) &&
                        (isNotBlank(kontaktinfoOriginal.getT1().getAdresse().getLandkode()) ||
                         isNotBlank(kontaktinfoOriginal.getT1().getAdresse().getAdresselinje1()))) {

                        processing = processing
                                .then(getAdresse(kontaktinfoOriginal.getT1())
                                        .doOnNext(kontaktinfo::setAdresse)
                                        .then());
                    }

                    return processing.thenReturn(kontaktinfo);
                });
    }

    private Mono<Void> leggTilPersonadresse(KontaktinformasjonForDoedsboDTO kontaktinfo, Map<String, String> poststedsnavn) {

        if (isNull(kontaktinfo.getAdresse()) || isBlank(kontaktinfo.getAdresse().getPostnummer())) {

            return personRepository.findByIdent(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer())
                    .map(DbPerson::getPerson)
                    .map(PersonDTO::getBostedsadresse)
                    .flatMapMany(Flux::fromIterable)
                    .next()
                    .map(bostedsadresse -> {
                        val context = MappingContextUtils.getMappingContext();
                        context.setProperty("poststedsnavn", poststedsnavn);
                        return mapperFacade.map(bostedsadresse, KontaktinformasjonForDoedsboAdresse.class, context);
                    })
                    .doOnNext(kontaktinfo::setAdresse)
                    .then();
        }
        return Mono.empty();
    }

    private Mono<KontaktinformasjonForDoedsboAdresse> getAdresse(KontaktinformasjonForDoedsboDTO kontaktinfo) {

        if (nonNull(kontaktinfo.getAdresse()) && isNotBlank(kontaktinfo.getAdresse().getAdresselinje1())) {
            return Mono.just(kontaktinfo.getAdresse());

        } else {
            if (nonNull(kontaktinfo.getAdresse()) &&
                isNotBlank(kontaktinfo.getAdresse().getLandkode()) &&
                !"NOR".equals(kontaktinfo.getAdresse().getLandkode())) {
                return enkelAdresseService.getUtenlandskAdresse(new UtenlandskAdresseDTO(),
                                kontaktinfo.getAdresse().getLandkode(), kontaktinfo.getMaster())
                        .map(adresse -> mapperFacade.map(adresse, KontaktinformasjonForDoedsboAdresse.class));

            } else {
                return adresseServiceConsumer.getVegadresse(VegadresseDTO.builder()
                                .postnummer(nonNull(kontaktinfo.getAdresse()) ? kontaktinfo.getAdresse().getPostnummer() : null)
                                .build(), null)
                        .map(adresse -> mapperFacade.map(adresse, KontaktinformasjonForDoedsboAdresse.class));
            }
        }
    }

    private Mono<Void> setOrganisasjonsnavnOgAdresse(KontaktinformasjonForDoedsboDTO kontaktinfo, OrganisasjonDTO
            organisasjonDto) {

        return organisasjonForvalterConsumer.getOrganisasjoner(organisasjonDto.getOrganisasjonsnummer())
                .map(Map::entrySet)
                .flatMapMany(Flux::fromIterable)
                .filter(entry -> "q1".equals(entry.getKey()) || "q2".equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .next()
                .doOnNext(organisasjon -> {
                    organisasjonDto.setOrganisasjonsnavn((String) organisasjon.get("organisasjonsnavn"));
                    if (isNull(kontaktinfo.getAdresse()) || isBlank(kontaktinfo.getAdresse().getPostnummer())) {
                        kontaktinfo.setAdresse(!((List<Map<String,String>>) organisasjon.get("adresser")).isEmpty() ?
                                mapperFacade.map(((List<Map<String,String>>) organisasjon.get("adresser")).getFirst(),
                                        KontaktinformasjonForDoedsboAdresse.class) :
                                null);
                    }
                })
                .then();
    }

    private Mono<Void> leggTilPersonnavn(KontaktpersonDTO kontaktpersonDTO) {

        return hentPersonnavn(kontaktpersonDTO.getNavn())
                .doOnNext(kontaktpersonDTO::setNavn)
                .then();
    }

    private Mono<Void> leggTilPersonnavn(OrganisasjonDTO organisasjon) {

        return hentPersonnavn(organisasjon.getKontaktperson())
                .doOnNext(organisasjon::setKontaktperson)
                .then();
    }

    private Mono<PersonNavnDTO> hentPersonnavn(PersonNavnDTO personnavn) {

        val navn = isNull(personnavn) ? new PersonNavnDTO() : personnavn;
        if (isBlank(navn.getFornavn()) || isBlank(navn.getEtternavn()) ||
            (isBlank(navn.getMellomnavn()) && isTrue(navn.getHasMellomnavn()))) {

            return genererNavnServiceConsumer.getNavn()
                    .doOnNext(nyttNavn -> {
                        navn.setFornavn(blankCheck(navn.getFornavn(), nyttNavn.getAdjektiv()));
                        navn.setEtternavn(blankCheck(navn.getEtternavn(), nyttNavn.getSubstantiv()));
                        navn.setMellomnavn(blankCheck(navn.getMellomnavn(),
                                isTrue(navn.getHasMellomnavn()) ? nyttNavn.getAdverb() : null));
                    })
                    .thenReturn(navn);
        }

        return Mono.just(navn);
    }

    private Mono<Boolean> isValidPersonnavn(KontaktinformasjonForDoedsboDTO kontakt) {

        if (nonNull(kontakt.getPersonSomKontakt()) &&
            nonNull(kontakt.getPersonSomKontakt().getNavn())) {
            return isValid(kontakt.getPersonSomKontakt().getNavn());
        }

        if (nonNull(kontakt.getAdvokatSomKontakt()) &&
            nonNull(kontakt.getAdvokatSomKontakt().getKontaktperson())) {
            return isValid(kontakt.getAdvokatSomKontakt().getKontaktperson());
        }

        if (nonNull(kontakt.getOrganisasjonSomKontakt()) &&
            nonNull(kontakt.getOrganisasjonSomKontakt().getKontaktperson())) {
            return isValid(kontakt.getOrganisasjonSomKontakt().getKontaktperson());
        }

        return Mono.just(TRUE);
    }

    private Mono<Boolean> isValid(PersonNavnDTO navn) {

        if (isNotBlank(navn.getFornavn()) ||
            isNotBlank(navn.getMellomnavn()) ||
            isNotBlank(navn.getEtternavn())) {

            return genererNavnServiceConsumer.verifyNavn(NavnDTO.builder()
                    .adjektiv(navn.getFornavn())
                    .adverb(navn.getMellomnavn())
                    .substantiv(navn.getEtternavn())
                    .build());
        } else {
            return Mono.just(true);
        }
    }

    private boolean isValidOrganisasjonName(KontaktinformasjonForDoedsboDTO kontakt) {

        val advokat = isNull(kontakt.getAdvokatSomKontakt()) ||
                      isNotBlank(kontakt.getAdvokatSomKontakt().getOrganisasjonsnummer()) ||
                      isBlank(kontakt.getAdvokatSomKontakt().getOrganisasjonsnummer()) &&
                      isBlank(kontakt.getAdvokatSomKontakt().getOrganisasjonsnavn());

        val organisasjon = isNull(kontakt.getOrganisasjonSomKontakt()) ||
                           isNotBlank(kontakt.getOrganisasjonSomKontakt().getOrganisasjonsnummer()) ||
                           isBlank(kontakt.getOrganisasjonSomKontakt().getOrganisasjonsnummer()) &&
                           isBlank(kontakt.getOrganisasjonSomKontakt().getOrganisasjonsnavn());

        return advokat && organisasjon;
    }

    private Mono<Boolean> isValidOrganisasjonIMiljoe(OrganisasjonDTO pdlOrganisasjon) {

        if (isBlank(pdlOrganisasjon.getOrganisasjonsnummer())) {
            return Mono.just(false);
        }

        return organisasjonForvalterConsumer.getOrganisasjoner(pdlOrganisasjon.getOrganisasjonsnummer())
                .map(Map::entrySet)
                .flatMapMany(Flux::fromIterable)
                .filter(entry -> "q1".equals(entry.getKey()) || "q2".equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .filter(organisasjon -> isNull(pdlOrganisasjon.getOrganisasjonsnavn()) ||
                                        pdlOrganisasjon.getOrganisasjonsnavn().equalsIgnoreCase((String) organisasjon.get("organisasjonsnavn")))
                .hasElements();
    }

    private Mono<Void> leggTilNyAddressat(KontaktpersonDTO kontakt, String hovedperson) {

        if (isNull(kontakt.getNyKontaktperson())) {
            kontakt.setNyKontaktperson(new PersonRequestDTO());
        }

        if (isNull(kontakt.getNyKontaktperson().getAlder()) &&
            isNull(kontakt.getNyKontaktperson().getFoedtEtter()) &&
            isNull(kontakt.getNyKontaktperson().getFoedtFoer())) {

            kontakt.getNyKontaktperson().setFoedtFoer(LocalDateTime.now().minusYears(18));
            kontakt.getNyKontaktperson().setFoedtEtter(LocalDateTime.now().minusYears(75));
        }

        EgenskaperFraHovedperson.kopierData(hovedperson, kontakt.getNyKontaktperson());

        return createPersonService.execute(kontakt.getNyKontaktperson())
                .doOnNext(createdPerson ->
                        kontakt.setIdentifikasjonsnummer(createdPerson.getIdent()))
                .flatMap(createdPerson ->
                        relasjonService.setRelasjoner(hovedperson, RelasjonType.AVDOEDD_FOR_KONTAKT,
                                createdPerson.getIdent(), RelasjonType.KONTAKT_FOR_DOEDSBO));
    }
}