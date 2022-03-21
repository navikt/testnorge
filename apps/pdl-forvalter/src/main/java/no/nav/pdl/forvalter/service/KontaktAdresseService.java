package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class KontaktAdresseService extends AdresseService<KontaktadresseDTO, PersonDTO> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kontaktadresse: kun én adresse skal være satt (vegadresse, " +
            "postboksadresse, utenlandskAdresse)";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;
    private final DummyAdresseService dummyAdresseService;

    public KontaktAdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer,
                                 AdresseServiceConsumer adresseServiceConsumer, MapperFacade mapperFacade,
                                 DummyAdresseService dummyAdresseService) {
        super(genererNavnServiceConsumer);
        this.adresseServiceConsumer = adresseServiceConsumer;
        this.mapperFacade = mapperFacade;
        this.dummyAdresseService = dummyAdresseService;
    }

    private static void validatePostBoksAdresse(KontaktadresseDTO.PostboksadresseDTO postboksadresse) {
        if (isBlank(postboksadresse.getPostboks())) {
            throw new InvalidRequestException(VALIDATION_POSTBOKS_ERROR);
        }
        if (isBlank(postboksadresse.getPostnummer()) ||
                !postboksadresse.getPostnummer().matches("[0-9]{4}")) {
            throw new InvalidRequestException(VALIDATION_POSTNUMMER_ERROR);
        }
    }

    public List<KontaktadresseDTO> convert(PersonDTO person, Boolean relaxed) {

        for (var adresse : person.getKontaktadresse()) {

            if (isTrue(adresse.getIsNew())) {

                if (isNotTrue(relaxed)) {
                    handle(adresse, person);
                }
                populateMiscFields(adresse, person);
            }
        }
        enforceIntegrity(person.getKontaktadresse());
        return person.getKontaktadresse();
    }

    @Override
    public void validate(KontaktadresseDTO adresse, PersonDTO person) {

        if (adresse.countAdresser() > 1) {
            throw new InvalidRequestException(VALIDATION_AMBIGUITY_ERROR);
        }
        if (nonNull(adresse.getVegadresse()) && isNotBlank(adresse.getVegadresse().getBruksenhetsnummer())) {
            validateBruksenhet(adresse.getVegadresse().getBruksenhetsnummer());
        }
        if (isNull(adresse.getAdresseIdentifikatorFraMatrikkelen()) &&
                nonNull(adresse.getVegadresse()) && nonNull(adresse.getVegadresse().getAdressenavn())) {
            validateMasterPdl(adresse);
        }
        if (nonNull(adresse.getPostboksadresse())) {
            validatePostBoksAdresse(adresse.getPostboksadresse());
        }
        if (nonNull(adresse.getOpprettCoAdresseNavn())) {
            validateCoAdresseNavn(adresse.getOpprettCoAdresseNavn());
        }
    }

    private void handle(KontaktadresseDTO kontaktadresse, PersonDTO person) {

        if (FNR == IdenttypeFraIdentUtility.getIdenttype(person.getIdent())) {

            if (STRENGT_FORTROLIG == person.getAdressebeskyttelse().stream()
                    .findFirst().orElse(new AdressebeskyttelseDTO()).getGradering()) {
                return;

            } else if (kontaktadresse.countAdresser() == 0) {
                kontaktadresse.setVegadresse(new VegadresseDTO());
            }

        } else if (kontaktadresse.countAdresser() == 0) {
            kontaktadresse.setUtenlandskAdresse(new UtenlandskAdresseDTO());
        }

        if (nonNull(kontaktadresse.getVegadresse())) {
            var vegadresse =
                    adresseServiceConsumer.getVegadresse(kontaktadresse.getVegadresse(), kontaktadresse.getAdresseIdentifikatorFraMatrikkelen());
            kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, kontaktadresse.getVegadresse());

        } else if (nonNull(kontaktadresse.getUtenlandskAdresse()) &&
                kontaktadresse.getUtenlandskAdresse().isEmpty()) {

            kontaktadresse.setMaster(DbVersjonDTO.Master.PDL);
            kontaktadresse.setGyldigFraOgMed(now());
            kontaktadresse.setGyldigTilOgMed(now().plusYears(1));
            kontaktadresse.setUtenlandskAdresse(dummyAdresseService.getUtenlandskAdresse(getLandkode(person)));
        }

        kontaktadresse.setCoAdressenavn(genererCoNavn(kontaktadresse.getOpprettCoAdresseNavn()));
        kontaktadresse.setOpprettCoAdresseNavn(null);
    }

    private String getLandkode(PersonDTO person) {

        return Stream.of(person.getKontaktadresse().stream()
                                .filter(adresse -> nonNull(adresse.getUtenlandskAdresse()))
                                .filter(adresse -> isNotBlank(adresse.getUtenlandskAdresse().getLandkode()))
                                .map(KontaktadresseDTO::getUtenlandskAdresse)
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
