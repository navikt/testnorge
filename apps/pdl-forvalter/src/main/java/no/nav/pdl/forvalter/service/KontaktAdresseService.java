package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.domain.PdlKontaktadresse;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.PDL;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.count;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class KontaktAdresseService {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (postboksadresse, " +
            "utenlandskAdresse, vegadresse, vegadresseForPost, postadresseIFrittFormat, utenlandskAdresseIFrittFormat)";
    private static final String VALIDATION_DEPRECATED_ERROR = "PostadresseIFrittFormat og utenlandskAdresseIFrittFormat " +
            "er foreldet og skal ikke benyttes";
    private static final String VALIDATION_MASTER_PDL_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi " +
            "hvis master er PDL";

    private final MapperFacade mapperFacade;
    private final VegadresseService vegadresseService;

    public List<PdlKontaktadresse> resolve(List<PdlKontaktadresse> request) {

        for (PdlKontaktadresse kontaktadresse : request) {

            if (isNull(kontaktadresse.getId()) || kontaktadresse.getId().equals(0)) {
                validateAdresse(kontaktadresse);

                if (nonNull(kontaktadresse.getVegadresse())) {
                    var vegadresse =
                            vegadresseService.get(kontaktadresse.getVegadresse(), kontaktadresse.getAdresseIdentifikatorFraMatrikkelen());
                    kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
                    kontaktadresse.setVegadresse(mapperFacade.map(vegadresse, PdlVegadresse.class));
                }
            }
        }
        return request;
    }

    private void validateAdresse(PdlKontaktadresse adresse) {
        if (count(adresse.getPostadresseIFrittFormat()) +
                count(adresse.getPostboksadresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getUtenlandskAdresseIFrittFormat()) +
                count(adresse.getVegadresse()) +
                count(adresse.getVegadresseForPost()) > 1) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_AMBIGUITY_ERROR);
        }
        if (nonNull(adresse.getUtenlandskAdresseIFrittFormat()) ||
                nonNull(adresse.getPostadresseIFrittFormat())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_DEPRECATED_ERROR);
        }
        if (PDL.equals(adresse.getMaster()) &&
                (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed()))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_MASTER_PDL_ERROR);
        }
    }
}
