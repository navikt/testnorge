package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.data.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class OppholdsadresseService extends AdresseService<OppholdsadresseDTO, PersonDTO> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Oppholdsadresse: kun én adresse skal være satt (vegadresse, " +
            "matrikkeladresse, utenlandskAdresse)";
    private static final String VALIDATION_PROTECTED_ADDRESS = "Oppholdsadresse: Personer med adressebeskyttelse == " +
            "STRENGT_FORTROLIG skal ikke ha oppholdsadresse";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;
    private final EnkelAdresseService enkelAdresseService;

    public OppholdsadresseService(GenererNavnServiceConsumer genererNavnServiceConsumer,
                                  AdresseServiceConsumer adresseServiceConsumer, MapperFacade mapperFacade,
                                  EnkelAdresseService enkelAdresseService) {
        super(genererNavnServiceConsumer);
        this.adresseServiceConsumer = adresseServiceConsumer;
        this.mapperFacade = mapperFacade;
        this.enkelAdresseService = enkelAdresseService;
    }

    public List<OppholdsadresseDTO> convert(PersonDTO person) {

        for (var adresse : person.getOppholdsadresse()) {

            if (isTrue(adresse.getIsNew())) {

                adresse.setKilde(getKilde(adresse));
                adresse.setMaster(getMaster(adresse, person));
                handle(adresse, person);
            }
        }

        return person.getOppholdsadresse();
    }

    @Override
    public void validate(OppholdsadresseDTO adresse, PersonDTO person) {

        if (adresse.countAdresser() > 1) {
            throw new InvalidRequestException(VALIDATION_AMBIGUITY_ERROR);

        }
        if (FNR == IdenttypeFraIdentUtility.getIdenttype(person.getIdent()) &&
                STRENGT_FORTROLIG == person.getAdressebeskyttelse().stream()
                        .findFirst().orElse(new AdressebeskyttelseDTO()).getGradering() &&
                adresse.countAdresser() > 0) {
            throw new InvalidRequestException(VALIDATION_PROTECTED_ADDRESS);
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

    protected void handle(OppholdsadresseDTO oppholdsadresse, PersonDTO person) {

        if (FNR == IdenttypeFraIdentUtility.getIdenttype(person.getIdent())) {

            if (STRENGT_FORTROLIG == person.getAdressebeskyttelse().stream()
                    .findFirst().orElse(new AdressebeskyttelseDTO()).getGradering()) {

                return;

            } else if (oppholdsadresse.countAdresser() == 0) {
                oppholdsadresse.setVegadresse(new VegadresseDTO());
            }

        } else if (oppholdsadresse.countAdresser() == 0) {
            oppholdsadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());
        }

        if (nonNull(oppholdsadresse.getVegadresse())) {
            var vegadresse =
                    adresseServiceConsumer.getVegadresse(oppholdsadresse.getVegadresse(), oppholdsadresse.getAdresseIdentifikatorFraMatrikkelen());
            oppholdsadresse.setAdresseIdentifikatorFraMatrikkelen(oppholdsadresse.getMaster() == Master.FREG ?
                    vegadresse.getMatrikkelId() : null);
            mapperFacade.map(vegadresse, oppholdsadresse.getVegadresse());

        } else if (nonNull(oppholdsadresse.getMatrikkeladresse())) {
            var matrikkeladresse =
                    adresseServiceConsumer.getMatrikkeladresse(oppholdsadresse.getMatrikkeladresse(), oppholdsadresse.getAdresseIdentifikatorFraMatrikkelen());
            oppholdsadresse.setAdresseIdentifikatorFraMatrikkelen(matrikkeladresse.getMatrikkelId());
            mapperFacade.map(matrikkeladresse, oppholdsadresse.getMatrikkeladresse());

        } else if (nonNull(oppholdsadresse.getUtenlandskAdresse())) {

            oppholdsadresse.setUtenlandskAdresse(enkelAdresseService.getUtenlandskAdresse(
                    oppholdsadresse.getUtenlandskAdresse(), getLandkode(person), oppholdsadresse.getMaster()));
        }

        oppholdsadresse.setCoAdressenavn(genererCoNavn(oppholdsadresse.getOpprettCoAdresseNavn()));
        oppholdsadresse.setOpprettCoAdresseNavn(null);
    }

    private String getLandkode(PersonDTO person) {

        return Stream.of(person.getOppholdsadresse().stream()
                                .filter(adresse -> nonNull(adresse.getUtenlandskAdresse()))
                                .filter(adresse -> isNotBlank(adresse.getUtenlandskAdresse().getLandkode()))
                                .map(OppholdsadresseDTO::getUtenlandskAdresse)
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
