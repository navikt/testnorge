package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.AdresseDTO.Master.PDL;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
@RequiredArgsConstructor
public class KontaktAdresseService extends AdresseService<KontaktadresseDTO> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (vegadresse, " +
            "postboksadresse, utenlandskAdresse)";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;

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
    }

    @Override
    protected void handle(KontaktadresseDTO kontaktadresse) {
        if (nonNull(kontaktadresse.getVegadresse())) {
            var vegadresse =
                    adresseServiceConsumer.getAdresse(kontaktadresse.getVegadresse(), kontaktadresse.getAdresseIdentifikatorFraMatrikkelen());
            kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, kontaktadresse.getVegadresse());
        }
    }
}
