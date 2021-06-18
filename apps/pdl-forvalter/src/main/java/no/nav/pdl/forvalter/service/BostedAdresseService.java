package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.AdresseDTO.Master.FREG;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.AdresseDTO.Master.PDL;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class BostedAdresseService extends AdresseService<BostedadresseDTO> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (vegadresse, " +
            "matrikkeladresse, ukjentbosted, utenlandskAdresse)";
    private static final String VALIDATION_ADDRESS_ABSENT_ERROR = "Én av adressene må velges (vegadresse, " +
            "matrikkeladresse, ukjentbosted, utenlandskAdresse)";
    private static final String VALIDATION_MASTER_PDL_ERROR = "Utenlandsk adresse krever at master er PDL";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;

    @Override
    protected void validate(BostedadresseDTO adresse) {

        if (count(adresse.getMatrikkeladresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) +
                count(adresse.getUkjentBosted()) > 1) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_AMBIGUITY_ERROR);

        } else if (count(adresse.getMatrikkeladresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) +
                count(adresse.getUkjentBosted()) == 0) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_ADDRESS_ABSENT_ERROR);
        }

        if (FREG.equals(adresse.getMaster()) && (nonNull(adresse.getUtenlandskAdresse()))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_MASTER_PDL_ERROR);
        }
        if (nonNull(adresse.getVegadresse()) && isNotBlank(adresse.getVegadresse().getBruksenhetsnummer())) {
            validateBruksenhet(adresse.getVegadresse().getBruksenhetsnummer());
        }
        if (nonNull(adresse.getMatrikkeladresse()) && isNotBlank(adresse.getMatrikkeladresse().getBruksenhetsnummer())) {
            validateBruksenhet(adresse.getMatrikkeladresse().getBruksenhetsnummer());
        }
        if (nonNull(adresse.getGyldigFraOgMed()) && nonNull(adresse.getGyldigTilOgMed()) &&
                !adresse.getGyldigFraOgMed().isBefore(adresse.getGyldigTilOgMed())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_ADRESSE_OVELAP_ERROR);
        }
    }

    @Override
    protected void handle(BostedadresseDTO bostedadresse) {
        if (nonNull(bostedadresse.getVegadresse())) {
            var vegadresse =
                    adresseServiceConsumer.getAdresse(bostedadresse.getVegadresse(), bostedadresse.getAdresseIdentifikatorFraMatrikkelen());
            bostedadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, bostedadresse.getVegadresse());
        }
        if (nonNull(bostedadresse.getUtenlandskAdresse())) {
            bostedadresse.setMaster(PDL);
        }
    }
}
