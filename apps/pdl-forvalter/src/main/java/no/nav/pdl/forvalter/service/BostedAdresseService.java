package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.AdresseDTO.Master.FREG;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.AdresseDTO.Master.PDL;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class BostedAdresseService extends AdresseService<BostedadresseDTO> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (vegadresse, " +
            "matrikkeladresse, ukjentbosted, utenlandskAdresse)";
    private static final String VALIDATION_ADDRESS_ABSENT_ERROR = "Én av adressene må velges (vegadresse, " +
            "matrikkeladresse, ukjentbosted, utenlandskAdresse)";
    private static final String VALIDATION_MASTER_PDL_ERROR = "Utenlandsk adresse krever at master er PDL";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;

    public BostedAdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer, AdresseServiceConsumer adresseServiceConsumer, MapperFacade mapperFacade) {
        super(genererNavnServiceConsumer);
        this.adresseServiceConsumer = adresseServiceConsumer;
        this.mapperFacade = mapperFacade;
    }

    @Override
    protected void validate(BostedadresseDTO adresse) {

        if (count(adresse.getMatrikkeladresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) +
                count(adresse.getUkjentBosted()) > 1) {
            throw new InvalidRequestException(VALIDATION_AMBIGUITY_ERROR);

        } else if (count(adresse.getMatrikkeladresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) +
                count(adresse.getUkjentBosted()) == 0) {
            throw new InvalidRequestException(VALIDATION_ADDRESS_ABSENT_ERROR);
        }

        if (FREG.equals(adresse.getMaster()) && (nonNull(adresse.getUtenlandskAdresse()))) {
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

    @Override
    protected void handle(BostedadresseDTO bostedadresse) {

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

        } else if (nonNull(bostedadresse.getUtenlandskAdresse())) {
            bostedadresse.setMaster(PDL);
        }

        bostedadresse.setCoAdressenavn(genererCoNavn(bostedadresse.getOpprettCoAdresseNavn()));
        bostedadresse.setOpprettCoAdresseNavn(null);
    }
}
