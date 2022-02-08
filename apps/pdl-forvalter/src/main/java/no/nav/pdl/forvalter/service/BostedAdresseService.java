package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility.getIdenttype;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class BostedAdresseService extends AdresseService<BostedadresseDTO, PersonDTO> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Bostedsadresse: kun én adresse skal være satt (vegadresse, " +
            "matrikkeladresse, ukjentbosted, utenlandskAdresse)";
    private static final String VALIDATION_PROTECTED_ADDRESS = "Bostedsadresse: Personer med adressebeskyttelse == " +
            "STRENGT_FORTROLIG skal ikke ha bostedsadresse";
    private static final String VALIDATION_MASTER_PDL_ERROR = "Bostedsadresse: utenlandsk adresse krever at master er PDL";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final DummyAdresseService dummyAdresseService;
    private final MapperFacade mapperFacade;

    public BostedAdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer, AdresseServiceConsumer adresseServiceConsumer, DummyAdresseService dummyAdresseService, MapperFacade mapperFacade) {
        super(genererNavnServiceConsumer);
        this.adresseServiceConsumer = adresseServiceConsumer;
        this.mapperFacade = mapperFacade;
        this.dummyAdresseService = dummyAdresseService;
    }

    public List<BostedadresseDTO> convert(PersonDTO person, Boolean relaxed) {

        for (var adresse : person.getBostedsadresse()) {

            if (isTrue(adresse.getIsNew())) {

                populateMiscFields(adresse, person);
                if (isNotTrue(relaxed)) {
                    handle(adresse, person);
                }
            }
        }
        enforceIntegrity(person.getBostedsadresse());
        return person.getBostedsadresse();
    }

    @Override
    public void validate(BostedadresseDTO adresse, PersonDTO person) {

        if (adresse.countAdresser() > 1) {
            throw new InvalidRequestException(VALIDATION_AMBIGUITY_ERROR);
        }
        if (FNR == IdenttypeFraIdentUtility.getIdenttype(person.getIdent()) &&
                STRENGT_FORTROLIG == person.getAdressebeskyttelse().stream()
                        .findFirst().orElse(new AdressebeskyttelseDTO()).getGradering() &&
                adresse.countAdresser() > 0) {
            throw new InvalidRequestException(VALIDATION_PROTECTED_ADDRESS);
        }
        if (DbVersjonDTO.Master.FREG == adresse.getMaster() && nonNull(adresse.getUtenlandskAdresse())) {
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
                return;

            } else if (bostedadresse.countAdresser() == 0) {
                bostedadresse.setVegadresse(new VegadresseDTO());
            }

        } else if (bostedadresse.countAdresser() == 0) {

            bostedadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());
        }

        if (nonNull(bostedadresse.getVegadresse())) {

            var vegadresse =
                    adresseServiceConsumer.getVegadresse(bostedadresse.getVegadresse(), bostedadresse.getAdresseIdentifikatorFraMatrikkelen());
            bostedadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, bostedadresse.getVegadresse());

        } else if (nonNull(bostedadresse.getMatrikkeladresse())) {

            var matrikkeladresse =
                    adresseServiceConsumer.getMatrikkeladresse(bostedadresse.getMatrikkeladresse(), bostedadresse.getAdresseIdentifikatorFraMatrikkelen());
            bostedadresse.setAdresseIdentifikatorFraMatrikkelen(matrikkeladresse.getMatrikkelId());
            mapperFacade.map(matrikkeladresse, bostedadresse.getMatrikkeladresse());

        } else if (nonNull(bostedadresse.getUtenlandskAdresse()) && bostedadresse.getUtenlandskAdresse().isEmpty()) {

            bostedadresse.setMaster(DbVersjonDTO.Master.PDL);
            bostedadresse.setUtenlandskAdresse(dummyAdresseService.getUtenlandskAdresse(getLandkode(person)));
        }

        bostedadresse.setCoAdressenavn(genererCoNavn(bostedadresse.getOpprettCoAdresseNavn()));
        bostedadresse.setOpprettCoAdresseNavn(null);

        if (isNull(bostedadresse.getAngittFlyttedato())) {
            bostedadresse.setAngittFlyttedato(bostedadresse.getGyldigFraOgMed());
        }
    }

    private String getLandkode(PersonDTO person) {

        return Stream.of(person.getBostedsadresse().stream()
                                .filter(adresse -> isNotBlank(adresse.getUtenlandskAdresse().getLandkode()))
                                .map(BostedadresseDTO::getUtenlandskAdresse)
                                .map(UtenlandskAdresseDTO::getLandkode)
                                .findFirst(),
                        person.getUtflytting().stream()
                                .map(UtflyttingDTO::getTilflyttingsland)
                                .findFirst(),
                        person.getStatsborgerskap().stream()
                                .filter(statsborger -> "NOR".equals(statsborger.getLandkode()))
                                .map(StatsborgerskapDTO::getLandkode)
                                .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst().orElse(null);
    }
}
