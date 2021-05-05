package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.dto.RsKontaktadresse;
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
public class KontaktAdresseService {

    private static final String VALIDATION_AMBIGUITY_ERROR = "Kun én adresse skal være satt (postboksadresse, " +
            "utenlandskAdresse, vegadresse)";
    private static final String VALIDATION_MASTER_PDL_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi " +
            "hvis master er PDL";
    private static final String VALIDATION_BRUKSENHET_ERROR = "Bruksenhetsnummer identifiserer en boligenhet innenfor et " +
            "bygg eller en bygningsdel. Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre";
    private static final String VALIDATION_POSTBOKS_ERROR = "Alfanumerisk identifikator av postboks. Kan ikke være tom";
    private static final String VALIDATION_POSTNUMMER_ERROR = "Postnummer består av fire sifre";
    private static final String VALIDATION_GYLDIGHET_ABSENT_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha " +
            "verdi for vegadresse uten matrikkelId";

    private final MapperFacade mapperFacade;
    private final VegadresseService vegadresseService;

    private static void validateAdresse(RsKontaktadresse adresse) {
        if (count(adresse.getPostboksadresse()) +
                count(adresse.getUtenlandskAdresse()) +
                count(adresse.getVegadresse()) > 1) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_AMBIGUITY_ERROR);
        }
        if (PDL.equals(adresse.getMaster()) &&
                (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed()))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_MASTER_PDL_ERROR);
        }
        if (nonNull(adresse.getVegadresse()) && isNotBlank(adresse.getVegadresse().getBruksenhetsnummer())) {
            validateBruksenhet(adresse.getVegadresse().getBruksenhetsnummer());
            validateMasterPdl(adresse);
        }
        if (isNull(adresse.getAdresseIdentifikatorFraMatrikkelen()) &&
                nonNull(adresse.getVegadresse()) && nonNull(adresse.getVegadresse().getAdressenavn())) {
            validateMasterPdl(adresse);
        }
        if (nonNull(adresse.getPostboksadresse())) {
            validatePostBoksAdresse(adresse.getPostboksadresse());
        }
    }

    private static void validateMasterPdl(RsKontaktadresse adresse) {
        if (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_GYLDIGHET_ABSENT_ERROR);
        }
    }

    private static void validatePostBoksAdresse(RsKontaktadresse.Postboksadresse postboksadresse) {
        if (isBlank(postboksadresse.getPostboks())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_POSTBOKS_ERROR);
        }
        if (isBlank(postboksadresse.getPostnummer()) ||
                isNotBlank(postboksadresse.getPostnummer().replaceAll("[0-9]{4}", ""))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_POSTNUMMER_ERROR);
        }
    }

    private static void validateBruksenhet(String bruksenhet) {
        if (isNotBlank(bruksenhet.replaceAll("[HULK][0-9]{4}", ""))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_BRUKSENHET_ERROR);
        }
    }

    public List<RsKontaktadresse> resolve(List<RsKontaktadresse> request) {

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
