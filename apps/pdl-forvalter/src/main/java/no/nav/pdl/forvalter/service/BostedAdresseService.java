package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.FREG;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.count;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class BostedAdresseService extends AdresseService<PdlBostedadresse> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (vegadresse, " +
            "matrikkeladresse, ukjentbosted, utenlandskAdresse)";
    private static final String VALIDATION_MASTER_PDL_ERROR = "Utenlandsk Adresse krever at master er PDL";

    private final MapperFacade mapperFacade;
    private final VegadresseService vegadresseService;

    protected void validateAdresse(PdlBostedadresse adresse) {
        if (count(adresse.getMatrikkeladresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) +
                count(adresse.getUkjentBosted()) > 1) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_AMBIGUITY_ERROR);
        }
        if (FREG.equals(adresse.getMaster()) && (nonNull(adresse.getUtenlandskAdresse()))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_MASTER_PDL_ERROR);
        }
        if (nonNull(adresse.getVegadresse()) && isNotBlank(adresse.getVegadresse().getBruksenhetsnummer())) {
            validateBruksenhet(adresse.getVegadresse().getBruksenhetsnummer());
        }
        if (isNull(adresse.getAdresseIdentifikatorFraMatrikkelen()) &&
                nonNull(adresse.getVegadresse()) && nonNull(adresse.getVegadresse().getAdressenavn())) {
            validateMasterPdl(adresse);
        }
    }

    @Override
    public List<PdlBostedadresse> resolve(List<PdlBostedadresse> request) {

        for (var kontaktadresse : request) {

            if (isNull(kontaktadresse.getId()) || kontaktadresse.getId().equals(0)) {
                validateAdresse(kontaktadresse);

                if (nonNull(kontaktadresse.getVegadresse())) {
                    var vegadresse =
                            vegadresseService.get(kontaktadresse.getVegadresse(), kontaktadresse.getAdresseIdentifikatorFraMatrikkelen());
                    kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
                    mapperFacade.map(vegadresse, kontaktadresse.getVegadresse());
                }
                if (isBlank(kontaktadresse.getKilde())) {
                    kontaktadresse.setKilde("Dolly");
                }
            }
        }
        return request;
    }
}
