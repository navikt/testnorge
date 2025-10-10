package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
import static no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.FNR;
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

    public List<BostedadresseDTO> convert(PersonDTO person, Boolean relaxed) {

        person.getBostedsadresse().stream()
                .filter(adresse -> isTrue(adresse.getIsNew()) && (isNotTrue(relaxed)))
                .forEach(adresse -> {
                    handle(adresse, person);
                    adresse.setKilde(getKilde(adresse));
                    adresse.setMaster(getMaster(adresse, person));
                });

        oppdaterAdressedatoer(person.getBostedsadresse(), person);
        setAngittFlyttedato(person.getBostedsadresse());

        return person.getBostedsadresse();
    }

    @Override
    public void validate(BostedadresseDTO adresse, PersonDTO person) {

        if (adresse.countAdresser() > 1) {
            throw new InvalidRequestException(VALIDATION_AMBIGUITY_ERROR);
        }
        if (Master.FREG == adresse.getMaster() && nonNull(adresse.getUtenlandskAdresse())) {
            throw new InvalidRequestException(VALIDATION_MASTER_PDL_ERROR);
        }
        if (nonNull(adresse.getVegadresse()) && isNotBlank(adresse.getVegadresse().getBruksenhetsnummer())) {
            validateBruksenhet(adresse.getVegadresse().getBruksenhetsnummer());
        }
        if (nonNull(adresse.getMatrikkeladresse()) && isNotBlank(adresse.getMatrikkeladresse().getBruksenhetsnummer())) {
            validateBruksenhet(adresse.getMatrikkeladresse().getBruksenhetsnummer());
        }
        if (nonNull(adresse.getGyldigFraOgMed()) && nonNull(adresse.getGyldigTilOgMed()) &&
                !adresse.getGyldigFraOgMed().isBefore(adresse.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_ADRESSE_OVELAP_ERROR);
        }
        if (nonNull(adresse.getOpprettCoAdresseNavn())) {
            validateCoAdresseNavn(adresse.getOpprettCoAdresseNavn());
        }
    }

    private void handle(BostedadresseDTO bostedadresse, PersonDTO person) {

        if (FNR == getIdenttype(person.getIdent())) {

            if (STRENGT_FORTROLIG == person.getAdressebeskyttelse().stream()
                    .findFirst().orElse(new AdressebeskyttelseDTO()).getGradering()) {

                person.setBostedsadresse(null);
                return;

            } else if (!person.getUtflytting().isEmpty() && bostedadresse.countAdresser() == 0 &&
                    person.getInnflytting().stream()
                            .noneMatch(innflytting -> person.getUtflytting().stream()
                                    .anyMatch(utflytting -> innflytting.getInnflyttingsdato()
                                            .isAfter(utflytting.getUtflyttingsdato())))) {

                if (person.getUtflytting().getFirst().isVelkjentLand()) {
                    if (isNull(bostedadresse.getUtenlandskAdresse())) {
                        bostedadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());
                    }

                } else {
                    person.setBostedsadresse(new ArrayList<>(person.getBostedsadresse().stream()
                            .filter(adresse -> isNotTrue(adresse.getIsNew()))
                            .toList()));
                    return;
                }

            } else if (bostedadresse.countAdresser() == 0) {

                if (isTestnorgeIdent(person.getIdent())) {
                    bostedadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());

                } else {
                    bostedadresse.setVegadresse(new VegadresseDTO());
                }
            }

        } else if (bostedadresse.countAdresser() == 0) {

            if (person.getOppholdsadresse().isEmpty() &&
                    person.getKontaktadresse().isEmpty()) {

                bostedadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());
            } else {
                person.setBostedsadresse(null);
                return;
            }
        }

        buildBoadresse(bostedadresse, person);
    }

    private void buildBoadresse(BostedadresseDTO bostedadresse, PersonDTO person) {

        if (nonNull(bostedadresse.getVegadresse())) {

            var vegadresse =
                    adresseServiceConsumer.getVegadresse(bostedadresse.getVegadresse(), bostedadresse.getAdresseIdentifikatorFraMatrikkelen());
            bostedadresse.setAdresseIdentifikatorFraMatrikkelen(getMatrikkelId(bostedadresse, person.getIdent(), vegadresse.getMatrikkelId()));
            mapperFacade.map(vegadresse, bostedadresse.getVegadresse());

        } else if (nonNull(bostedadresse.getMatrikkeladresse())) {

            var matrikkeladresse =
                    adresseServiceConsumer.getMatrikkeladresse(bostedadresse.getMatrikkeladresse(), bostedadresse.getAdresseIdentifikatorFraMatrikkelen());
            bostedadresse.setAdresseIdentifikatorFraMatrikkelen(getMatrikkelId(bostedadresse, person.getIdent(), matrikkeladresse.getMatrikkelId()));
            mapperFacade.map(matrikkeladresse, bostedadresse.getMatrikkeladresse());

        } else if (nonNull(bostedadresse.getUtenlandskAdresse())) {

            bostedadresse.setMaster(Master.PDL);

            bostedadresse.setUtenlandskAdresse(enkelAdresseService.getUtenlandskAdresse(bostedadresse.getUtenlandskAdresse(), getLandkode(person),
                    bostedadresse.getMaster()));
        }

        bostedadresse.setCoAdressenavn(genererCoNavn(bostedadresse.getOpprettCoAdresseNavn()));
        bostedadresse.setOpprettCoAdresseNavn(null);

        if (isNull(bostedadresse.getAngittFlyttedato())) {
            bostedadresse.setAngittFlyttedato(bostedadresse.getGyldigFraOgMed());
        }
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
