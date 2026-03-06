package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class KontaktAdresseService extends AdresseService<KontaktadresseDTO, PersonDTO> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kontaktadresse: kun én adresse skal være satt (vegadresse, " +
                                                             "postboksadresse, utenlandskAdresse, postadresseIFrittFormat eller utenlandskAdresseIFrittFormat)";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;
    private final EnkelAdresseService enkelAdresseService;

    public KontaktAdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer,
                                 AdresseServiceConsumer adresseServiceConsumer, MapperFacade mapperFacade,
                                 EnkelAdresseService enkelAdresseService) {
        super(genererNavnServiceConsumer);
        this.adresseServiceConsumer = adresseServiceConsumer;
        this.mapperFacade = mapperFacade;
        this.enkelAdresseService = enkelAdresseService;
    }

    public Mono<Void> convert(PersonDTO person, Boolean relaxed) {

        return Flux.fromIterable(person.getKontaktadresse())
                .filter(adresse -> isTrue(adresse.getIsNew()) && (isNotTrue(relaxed)))
                .flatMap(adresse -> handle(adresse, person))
                .filter(Objects::nonNull)
                .doOnNext(adresse -> {
                    adresse.setKilde(getKilde(adresse));
                    adresse.setMaster(getMaster(adresse, person));
                })
                .collectList()
                .doOnNext(kontaktadresser -> {
                    person.setKontaktadresse(new ArrayList<>(kontaktadresser));
                    oppdaterAdressedatoer(person.getKontaktadresse(), person);
                })
                .then();
    }

    private static void validatePostBoksAdresse(KontaktadresseDTO.PostboksadresseDTO postboksadresse) {

        if (isBlank(postboksadresse.getPostboks())) {
            throw new InvalidRequestException(VALIDATION_POSTBOKS_ERROR);
        }
        if (isBlank(postboksadresse.getPostnummer()) ||
            !postboksadresse.getPostnummer().matches("\\d{4}")) {
            throw new InvalidRequestException(VALIDATION_POSTNUMMER_ERROR);
        }
    }

    @Override
    public Mono<Void> validate(KontaktadresseDTO adresse, PersonDTO person) {

        if (adresse.countAdresser() > 1) {
            throw new InvalidRequestException(VALIDATION_AMBIGUITY_ERROR);
        }
        if (nonNull(adresse.getVegadresse()) && isNotBlank(adresse.getVegadresse().getBruksenhetsnummer())) {
            validateBruksenhet(adresse.getVegadresse().getBruksenhetsnummer());
        }
        if (nonNull(adresse.getPostboksadresse())) {
            validatePostBoksAdresse(adresse.getPostboksadresse());
        }
        if (nonNull(adresse.getOpprettCoAdresseNavn())) {
            validateCoAdresseNavn(adresse.getOpprettCoAdresseNavn());
        }
        return Mono.empty();
    }

    private Mono<KontaktadresseDTO> handle(KontaktadresseDTO kontaktadresse, PersonDTO person) {

        return getKontaktadresse(kontaktadresse, person)
                .doOnNext(kontaktadresser -> {
                    if (Master.PDL == kontaktadresse.getMaster()) {
                        kontaktadresse.setGyldigFraOgMed(nonNull(kontaktadresse.getGyldigFraOgMed()) ? kontaktadresse.getGyldigFraOgMed() : now());
                        kontaktadresse.setGyldigTilOgMed(nonNull(kontaktadresse.getGyldigTilOgMed()) ? kontaktadresse.getGyldigTilOgMed() :
                                kontaktadresse.getGyldigFraOgMed().plusYears(1));
                    }
                    kontaktadresse.setCoAdressenavn(genererCoNavn(kontaktadresse.getOpprettCoAdresseNavn()));
                    kontaktadresse.setOpprettCoAdresseNavn(null);
                });
    }

    private Mono<KontaktadresseDTO> getKontaktadresse(KontaktadresseDTO kontaktadresse, PersonDTO person) {

        if (FNR == IdenttypeUtility.getIdenttype(person.getIdent())) {

            if (STRENGT_FORTROLIG == person.getAdressebeskyttelse().stream()
                    .findFirst().orElse(new AdressebeskyttelseDTO()).getGradering()) {
                return Mono.empty();

            } else if (kontaktadresse.countAdresser() == 0) {
                kontaktadresse.setVegadresse(new VegadresseDTO());
            }

        } else if (kontaktadresse.countAdresser() == 0) {
            kontaktadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());
        }

        if (nonNull(kontaktadresse.getVegadresse())) {
            return adresseServiceConsumer.getVegadresse(kontaktadresse.getVegadresse(), kontaktadresse.getAdresseIdentifikatorFraMatrikkelen())
                    .map(vegadresse -> {
                        kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(getMatrikkelId(kontaktadresse, person.getIdent(), vegadresse.getMatrikkelId()));
                        mapperFacade.map(vegadresse, kontaktadresse.getVegadresse());
                        kontaktadresse.getVegadresse().setKommunenummer(null);
                        return kontaktadresse;
                    });

        } else if (nonNull(kontaktadresse.getUtenlandskAdresse())) {

            kontaktadresse.setUtenlandskAdresse(enkelAdresseService.getUtenlandskAdresse(kontaktadresse.getUtenlandskAdresse(), getLandkode(person), kontaktadresse.getMaster()));

        } else if (nonNull(kontaktadresse.getUtenlandskAdresseIFrittFormat())) {

            kontaktadresse.setUtenlandskAdresseIFrittFormat(enkelAdresseService.getUtenlandskAdresse(kontaktadresse.getUtenlandskAdresseIFrittFormat(), getLandkode(person)));

        } else if (nonNull(kontaktadresse.getPostadresseIFrittFormat()) && kontaktadresse.getPostadresseIFrittFormat().isEmpty()) {

            return adresseServiceConsumer.getVegadresse(VegadresseDTO.builder()
                            .postnummer(kontaktadresse.getPostadresseIFrittFormat().getPostnummer())
                            .build(), kontaktadresse.getAdresseIdentifikatorFraMatrikkelen())
                    .map(vegadresse -> {
                        kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(getMatrikkelId(kontaktadresse, person.getIdent(), vegadresse.getMatrikkelId()));
                        mapperFacade.map(vegadresse, kontaktadresse.getPostadresseIFrittFormat());
                        return kontaktadresse;
                    });
        }

        return Mono.just(kontaktadresse);
    }

    private String getLandkode(PersonDTO person) {

        return Stream.of(person.getKontaktadresse().stream()
                                .map(KontaktadresseDTO::getUtenlandskAdresse)
                                .filter(Objects::nonNull)
                                .map(UtenlandskAdresseDTO::getLandkode)
                                .filter(StringUtils::isNotBlank)
                                .findFirst(),
                        person.getUtflytting().stream()
                                .map(UtflyttingDTO::getTilflyttingsland)
                                .findFirst(),
                        person.getStatsborgerskap().stream()
                                .map(StatsborgerskapDTO::getLandkode)
                                .filter(landkode -> !"NOR".equals(landkode))
                                .filter(StringUtils::isNotBlank)
                                .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst().orElse(null);
    }
}
