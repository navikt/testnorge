package no.nav.registre.hodejegeren.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AarsakskodeTilFeltnavnMapperService {

    @Autowired
    TpsStatusQuoService tpsStatusQuoService;

    public Map<String, String> getStatusQuoFraAarsakskode(AarsakskoderTrans1 aarsakskode, String aksjonsKode, String environment, String fnr) throws IOException {
        Map<String, String> personStatusQuo = new HashMap<>();

        switch (aarsakskode) {
        case NAVNEENDRING_FOERSTE:
        case NAVNEENDRING_MELDING:
        case NAVNEENDRING_KORREKSJON:
        case ADRESSEENDRING_UTEN_FLYTTING:
        case ADRESSEKORREKSJON:
        case UTVANDRING:
        case FARSKAP_MEDMORSKAP:
        case ENDRING_STATSBORGERSKAP_BIBEHOLD:
        case ENDRING_FAMILIENUMMER:
        case FOEDSELSNUMMERKORREKSJON:
        case ENDRING_FORELDREANSVAR:
        case ENDRING_OPPHOLDSTILLATELSE:
        case ENDRING_POSTADRESSE_TILLEGGSADRESSE:
        case ENDRING_POSTNUMMER_SKOLE_VALG_GRUNNKRETS:
        case KOMMUNEREGULERING:
        case ADRESSEENDRING_GAB:
        case ENDRING_KORREKSJON_FOEDESTED:
        case ENDRING_DUF_NUMMER:
        case FLYTTING_INNEN_KOMMUNEN:
        case FOEDSELSMELDING:
        case UREGISTRERT_PERSON:
            personStatusQuo.putAll(tpsStatusQuoService.getStatusQuo("FS03-FDNUMMER-PERSDATA-O",
                    Arrays.asList("datoDo", "statsborger"),
                    aksjonsKode, environment, fnr));
            break;
        case VIGSEL:
        case SEPERASJON:
        case SKILSMISSE:
        case SIVILSTANDSENDRING:
        case KORREKSJON_FAMILIEOPPLYSNINGER:
        case DOEDSMELDING:
            personStatusQuo.putAll(tpsStatusQuoService.getStatusQuo("FS03-FDNUMMER-PERSDATA-O",
                    Arrays.asList("datoDo", "statsborger", "sivilstand", "datoSivilstand"),
                    aksjonsKode, environment, fnr));
            personStatusQuo.putAll(tpsStatusQuoService.getStatusQuo("FS03-FDNUMMER-PERSRELA-O",
                    Arrays.asList("relasjon/fnrRelasjon", "relasjon/typeRelasjon"),
                    aksjonsKode, environment, fnr));
            break;
        case ANNULERING_FLYTTING_ADRESSEENDRING:
        case INNFLYTTING_ANNEN_KOMMUNE:
            personStatusQuo.putAll(tpsStatusQuoService.getStatusQuo("FS03-FDNUMMER-PERSDATA-O",
                    Arrays.asList("datoDo", "statsborger", "kommunenr", "datoFlyttet"),
                    aksjonsKode, environment, fnr));
            // FRA-KOMM-REGDATO
            break;
        case INNVANDRING:
        case TILDELING_DNUMMER:
        default:
            break;
        }

        return personStatusQuo;
    }
}