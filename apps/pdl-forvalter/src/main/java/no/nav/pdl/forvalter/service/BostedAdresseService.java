package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class BostedAdresseService extends AdresseService<BostedadresseDTO, PersonDTO> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Bostedsadresse: kun én adresse skal være satt (vegadresse, " +
                                                             "matrikkeladresse, ukjentbosted, utenlandskAdresse)";
    private static final String VALIDATION_MASTER_PDL_ERROR = "Bostedsadresse: utenlandsk adresse krever at master er PDL";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final EnkelAdresseService enkelAdresseService;
    private final MapperFacade mapperFacade;

    public BostedAdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer, AdresseServiceConsumer adresseServiceConsumer, EnkelAdresseService enkelAdresseService, MapperFacade mapperFacade) {
        super(genererNavnServiceConsumer);
        this.adresseServiceConsumer = adresseServiceConsumer;
        this.mapperFacade = mapperFacade;
        this.enkelAdresseService = enkelAdresseService;
    }

    public Mono<DbPerson> convert(DbPerson dbPerson, Boolean relaxed) {

        return Flux.fromIterable(dbPerson.getPerson().getBostedsadresse())
                .filter(adresse -> isTrue(adresse.getIsNew()) && (isNotTrue(relaxed)))
                .flatMap(adresse -> handle(adresse, dbPerson.getPerson()))
                .doOnNext(adresse -> {
                    adresse.setKilde(getKilde(adresse));
                    adresse.setMaster(getMaster(adresse, dbPerson.getPerson()));
                })
                .collectList()
                .doOnNext(adresser -> {
                    oppdaterAdressedatoer(dbPerson.getPerson().getBostedsadresse(), dbPerson.getPerson());
                    setAngittFlyttedato(dbPerson.getPerson().getBostedsadresse());
                })
                .thenReturn(dbPerson);
    }

    @Override
    public Mono<Void> validate(BostedadresseDTO adresse, PersonDTO person) {

        if (adresse.countAdresser() > 1) {
            return Mono.error(new InvalidRequestException(VALIDATION_AMBIGUITY_ERROR));
        }
        if (Master.FREG == adresse.getMaster() && nonNull(adresse.getUtenlandskAdresse())) {
            return Mono.error(new InvalidRequestException(VALIDATION_MASTER_PDL_ERROR));
        }
        if (nonNull(adresse.getVegadresse()) && isNotBlank(adresse.getVegadresse().getBruksenhetsnummer())) {
            return validateBruksenhet(adresse.getVegadresse().getBruksenhetsnummer());
        }
        if (nonNull(adresse.getMatrikkeladresse()) && isNotBlank(adresse.getMatrikkeladresse().getBruksenhetsnummer())) {
            return validateBruksenhet(adresse.getMatrikkeladresse().getBruksenhetsnummer());
        }
        if (nonNull(adresse.getGyldigFraOgMed()) && nonNull(adresse.getGyldigTilOgMed()) &&
            !adresse.getGyldigFraOgMed().isBefore(adresse.getGyldigTilOgMed())) {
            return Mono.error(new InvalidRequestException(VALIDATION_ADRESSE_OVELAP_ERROR));
        }
        if (nonNull(adresse.getOpprettCoAdresseNavn())) {
            return validateCoAdresseNavn(adresse.getOpprettCoAdresseNavn());
        }
        return Mono.empty();
    }

    private Mono<BostedadresseDTO> handle(BostedadresseDTO bostedadresse, PersonDTO person) {

        return Mono.defer(() -> {
                    if (FNR == getIdenttype(person.getIdent())) {

                        if (STRENGT_FORTROLIG == person.getAdressebeskyttelse().stream()
                                .findFirst().orElse(new AdressebeskyttelseDTO()).getGradering()) {

                            person.setBostedsadresse(null);
                            return Mono.empty();

                        } else if (!person.getUtflytting().isEmpty() && bostedadresse.countAdresser() == 0 &&
                            person.getInnflytting().stream()
                                    .noneMatch(innflytting -> person.getUtflytting().stream()
                                            .anyMatch(utflytting -> innflytting.getInnflyttingsdato()
                                                    .isAfter(utflytting.getUtflyttingsdato())))) {

                            if (person.getUtflytting().getFirst().isVelkjentLand() && isNull(bostedadresse.getUtenlandskAdresse())) {
                                bostedadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());
                            }

                        } else if (bostedadresse.countAdresser() == 0) {

                            if (isTestnorgeIdent(person.getIdent())) {
                                bostedadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());

                            } else {
                                bostedadresse.setVegadresse(new VegadresseDTO());
                            }
                        }

                    } else if (bostedadresse.countAdresser() == 0 &&
                               person.getOppholdsadresse().isEmpty() &&
                               person.getKontaktadresse().isEmpty()) {

                        bostedadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());
                    }
                    return Mono.just(bostedadresse);
                })
                .flatMap(bostedadresse1 -> buildBoadresse(bostedadresse1, person))
                .flatMap(boadresse -> genererCoNavn(boadresse.getOpprettCoAdresseNavn()))
                .doOnNext(adressseNavn -> {

                    bostedadresse.setCoAdressenavn(adressseNavn);
                    bostedadresse.setOpprettCoAdresseNavn(null);

                    if (isNull(bostedadresse.getAngittFlyttedato())) {
                        bostedadresse.setAngittFlyttedato(bostedadresse.getGyldigFraOgMed());
                    }
                })
                .thenReturn(bostedadresse);
    }

    private Mono<BostedadresseDTO> buildBoadresse(BostedadresseDTO bostedadresse, PersonDTO person) {

        if (nonNull(bostedadresse.getVegadresse())) {

            return adresseServiceConsumer.getVegadresse(bostedadresse.getVegadresse(), bostedadresse.getAdresseIdentifikatorFraMatrikkelen())
                    .flatMap(vegadresse -> {
                        bostedadresse.setAdresseIdentifikatorFraMatrikkelen(getMatrikkelId(bostedadresse, person.getIdent(), vegadresse.getMatrikkelId()));
                        mapperFacade.map(vegadresse, bostedadresse.getVegadresse());
                        return Mono.just(bostedadresse);
                    });

        } else if (nonNull(bostedadresse.getMatrikkeladresse())) {

            return adresseServiceConsumer.getMatrikkeladresse(bostedadresse.getMatrikkeladresse(), bostedadresse.getAdresseIdentifikatorFraMatrikkelen())
                    .flatMap(matrikkeladresse -> {
                        bostedadresse.setAdresseIdentifikatorFraMatrikkelen(getMatrikkelId(bostedadresse, person.getIdent(), matrikkeladresse.getMatrikkelId()));
                        mapperFacade.map(matrikkeladresse, bostedadresse.getMatrikkeladresse());
                        return Mono.just(bostedadresse);
                    });

        } else if (nonNull(bostedadresse.getUtenlandskAdresse())) {

            bostedadresse.setMaster(Master.PDL);

            return enkelAdresseService.getUtenlandskAdresse(bostedadresse.getUtenlandskAdresse(), getLandkode(person),
                            bostedadresse.getMaster())
                    .doOnNext(bostedadresse::setUtenlandskAdresse)
                    .thenReturn(bostedadresse);
        }

        return Mono.just(bostedadresse);
    }

    private String getLandkode(PersonDTO person) {

        return Stream.of(person.getBostedsadresse().stream()
                                .map(BostedadresseDTO::getUtenlandskAdresse)
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

    private static void setAngittFlyttedato(List<BostedadresseDTO> bostedsadresse) {

        bostedsadresse.forEach(adresse ->
                adresse.setAngittFlyttedato(adresse.getGyldigFraOgMed()));
    }
}
