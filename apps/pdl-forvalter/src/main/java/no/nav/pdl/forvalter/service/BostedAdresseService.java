package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.FREG;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.PDL;
import static no.nav.pdl.forvalter.service.AdresseServiceUtil.validateBruksenhet;
import static no.nav.pdl.forvalter.service.AdresseServiceUtil.validateMasterPdl;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.count;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class BostedAdresseService extends PdlArtifactService<PdlBostedadresse> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (vegadresse, " +
            "matrikkeladresse, ukjentbosted, utenlandskAdresse)";
    private static final String VALIDATION_MASTER_PDL_ERROR = "Utenlandsk Adresse krever at master er PDL";

    private final MapperFacade mapperFacade;
    private final VegadresseService vegadresseService;

    protected void validate(PdlBostedadresse adresse) {
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
    public void handle(PdlBostedadresse bostedadresse) {
        if (nonNull(bostedadresse.getVegadresse())) {
            var vegadresse =
                    vegadresseService.get(bostedadresse.getVegadresse(), bostedadresse.getAdresseIdentifikatorFraMatrikkelen());
            bostedadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, bostedadresse.getVegadresse());
        }
        if (nonNull(bostedadresse.getUtenlandskAdresse())) {
            bostedadresse.setMaster(PDL);
        }
    }
}
