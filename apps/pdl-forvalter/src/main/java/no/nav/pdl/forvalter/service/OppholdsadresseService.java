package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.domain.PdlOppholdsadresse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.PDL;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.count;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class OppholdsadresseService extends AdresseService<PdlOppholdsadresse> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (vegadresse, " +
            "matrikkeladresse, utenlandskAdresse)";

    private final VegadresseService vegadresseService;
    private final MapperFacade mapperFacade;

    @Override
    protected void validateAdresse(PdlOppholdsadresse adresse) {
        if (count(adresse.getVegadresse()) +
                count(adresse.getMatrikkeladresse()) +
                count(adresse.getUtenlandskAdresse()) > 1) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_AMBIGUITY_ERROR);
        }
        if (PDL.equals(adresse.getMaster()) &&
                (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed()))) {
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
    public List<PdlOppholdsadresse> resolve(List<PdlOppholdsadresse> request) {

        for (var oppholdsadresse : request) {

            if (isNull(oppholdsadresse.getId()) || oppholdsadresse.getId().equals(0)) {
                validateAdresse(oppholdsadresse);

                if (nonNull(oppholdsadresse.getVegadresse())) {
                    var vegadresse =
                            vegadresseService.get(oppholdsadresse.getVegadresse(), oppholdsadresse.getAdresseIdentifikatorFraMatrikkelen());
                    oppholdsadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
                    mapperFacade.map(vegadresse, oppholdsadresse.getVegadresse());
                }
                if (isBlank(oppholdsadresse.getKilde())) {
                    oppholdsadresse.setKilde("Dolly");
                }
            }
        }
        return request;
    }
}
