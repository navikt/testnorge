package no.nav.pdl.forvalter.service.command.pdlartifact;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.dto.RsKontaktadresse;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import no.nav.pdl.forvalter.utils.ArtifactUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.PDL;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.count;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.validateBruksenhet;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.validateMasterPdl;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class KontaktAdresseCommand extends PdlArtifactService<RsKontaktadresse> {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (vegadresse, " +
            "postboksadresse, utenlandskAdresse)";

    private final VegadresseService vegadresseService;
    private final MapperFacade mapperFacade;

    public KontaktAdresseCommand(List<RsKontaktadresse> request, VegadresseService vegadresseService, MapperFacade mapperFacade) {
        super(request);
        this.vegadresseService = vegadresseService;
        this.mapperFacade = mapperFacade;
    }

    protected static void validatePostBoksAdresse(RsKontaktadresse.Postboksadresse postboksadresse) {
        if (isBlank(postboksadresse.getPostboks())) {
            throw new HttpClientErrorException(BAD_REQUEST, ArtifactUtils.VALIDATION_POSTBOKS_ERROR);
        }
        if (isBlank(postboksadresse.getPostnummer()) ||
                !postboksadresse.getPostnummer().matches("[0-9]{4}")) {
            throw new HttpClientErrorException(BAD_REQUEST, ArtifactUtils.VALIDATION_POSTNUMMER_ERROR);
        }
    }

    @Override
    protected void validate(RsKontaktadresse adresse) {
        if (count(adresse.getPostboksadresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) > 1) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_AMBIGUITY_ERROR);
        }
        if (PDL.equals(adresse.getMaster()) &&
                (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed()))) {
            throw new HttpClientErrorException(BAD_REQUEST, ArtifactUtils.VALIDATION_MASTER_PDL_ERROR);
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
    protected void handle(RsKontaktadresse kontaktadresse) {
        if (nonNull(kontaktadresse.getVegadresse())) {
            var vegadresse =
                    vegadresseService.get(kontaktadresse.getVegadresse(), kontaktadresse.getAdresseIdentifikatorFraMatrikkelen());
            kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, kontaktadresse.getVegadresse());
        }
    }

    @Override
    protected void enforceIntegrity(List<RsKontaktadresse> type) {

    }
}
