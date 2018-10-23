package no.nav.registre.hodejegeren.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AarsakskodeTilFeltnavnMapperService {

    @Autowired
    TpsStatusQuoService tpsStatusQuoService;

    public Map<String, String> mapAarsakskodeTilFeltnavn(AarsakskoderTrans1 aarsakskoderTrans1, String aksjonsKode, String environment, String fnr) throws IOException {
        List<String> feltnavn = new ArrayList<>();

        switch (aarsakskoderTrans1) {
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
            feltnavn.add("datoDo");
            feltnavn.add("statsborger");
            break;
        case VIGSEL:
        case SEPERASJON:
        case SKILSMISSE:
        case SIVILSTANDSENDRING:
        case KORREKSJON_FAMILIEOPPLYSNINGER:
        case DOEDSMELDING:
            feltnavn.add("datoDo");
            feltnavn.add("statsborger");
            feltnavn.add("sivilstand");
            feltnavn.add("datoSivilstand");
            feltnavn.add("relasjon/fnrRelasjon");
            feltnavn.add("relasjon/typeRelasjon");
            break;
        case ANNULERING_FLYTTING_ADRESSEENDRING:
        case INNFLYTTING_ANNEN_KOMMUNE:
            feltnavn.add("datoDo");
            feltnavn.add("statsborger");
            // feltnavn.add("KOMMUNENR");
            feltnavn.add("datoFlyttet");
            // FRA-KOMM-REGDATO
            break;
        case INNVANDRING:
        case TILDELING_DNUMMER:
            return new HashMap<>();
        default:
            return new HashMap<>();
        }

        return tpsStatusQuoService.getStatusQuo(feltnavn, aksjonsKode, environment, fnr);
    }
}