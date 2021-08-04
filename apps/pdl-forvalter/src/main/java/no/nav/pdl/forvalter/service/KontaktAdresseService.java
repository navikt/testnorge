package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO.Master.PDL;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class KontaktAdresseService extends AdresseService<KontaktadresseDTO> {

    private static final String VALIDATION_ADDRESS_ABSENT_ERROR = "Én av adressene må velges (vegadresse, " +
            "postboksadresse, utenlandskAdresse)";
    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (vegadresse, " +
            "postboksadresse, utenlandskAdresse)";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;

    public KontaktAdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer, AdresseServiceConsumer adresseServiceConsumer, MapperFacade mapperFacade) {
        super(genererNavnServiceConsumer);
        this.adresseServiceConsumer = adresseServiceConsumer;
        this.mapperFacade = mapperFacade;
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

    @Override
    protected void validate(KontaktadresseDTO adresse) {
        if (count(adresse.getPostboksadresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) == 0) {
            throw new InvalidRequestException(VALIDATION_ADDRESS_ABSENT_ERROR);
        }
        if (count(adresse.getPostboksadresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) > 1) {
            throw new InvalidRequestException(VALIDATION_AMBIGUITY_ERROR);
        }
        if (PDL.equals(adresse.getMaster()) &&
                (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed()))) {
            throw new InvalidRequestException(VALIDATION_MASTER_PDL_ERROR);
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

    @Override
    protected void handle(KontaktadresseDTO kontaktadresse) {
        if (nonNull(kontaktadresse.getVegadresse())) {
            var vegadresse =
                    adresseServiceConsumer.getVegadresse(kontaktadresse.getVegadresse(), kontaktadresse.getAdresseIdentifikatorFraMatrikkelen());
            kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, kontaktadresse.getVegadresse());
        }

        kontaktadresse.setCoAdressenavn(genererCoNavn(kontaktadresse.getOpprettCoAdresseNavn()));
        kontaktadresse.setOpprettCoAdresseNavn(null);
    }
}
