package no.nav.registre.hodejegeren.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FunksjonTilFeltnavnMapperService {

    @Autowired
    TpsStatusQuoService tpsStatusQuoService;

    public Map<String, String> mapFunksjonTilFeltnavn(Funksjoner funksjoner, String aksjonsKode, String environment, String fnr) throws IOException {
        List<String> feltnavn = new ArrayList<>();

        switch (funksjoner) {
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
            feltnavn.add("datoDo");
            feltnavn.add("statsborger");
            break;
        case VIGSEL:
        case SEPERASJON:
        case SKILSMISSE:
        case SIVILSTANDSENDRING:
        case KORREKSJON_FAMILIEOPPLYSNINGER:
            feltnavn.add("datoDo");
            feltnavn.add("statsborger");
            feltnavn.add("sivilstand");
            feltnavn.add("datoSivilstand");
            break;
        case ANNULERING_FLYTTING_ADRESSEENDRING:
        case INNFLYTTING_ANNEN_KOMMUNE:
            feltnavn.add("datoDo");
            feltnavn.add("statsborger");
            feltnavn.add("kommunenr");
            feltnavn.add("tidligereKommunenr");
            feltnavn.add("datoFlyttet");
            feltnavn.add("boAdresse1");
            feltnavn.add("postAdresse1");
            feltnavn.add("boPoststed");
            break;
        case FOEDSELSMELDING:
        case INNVANDRING:
        case FLYTTING_INNEN_KOMMUNEN:
        case DOEDSMELDING:
        case UREGISTRERT_PERSON:
        case TILDELING_DNUMMER:
        case KORREKSJON_FAMILIEOPPLYSNINGER_BARN:
            feltnavn.add("");
            break;
        }

        return tpsStatusQuoService.getStatusQuo(feltnavn, aksjonsKode, environment, fnr);
    }
}