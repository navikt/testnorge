package no.nav.registre.hodejegeren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EndringskodeTilFeltnavnMapperService {

    public static final String DATO_DO = "datoDo";
    public static final String STATSBORGER = "statsborger"; // lagret i persdata
    public static final String STATSBORGERSKAP = "statsborgerskap"; // lagret i kerninfo
    public static final String NAV_ENHET = "kodeNAVenhet"; // lagret i kerninfo
    public static final String NAV_ENHET_BESKRIVELSE = "kodeNAVenhetBeskr"; // lagret i kerninfo
    public static final String FNR_RELASJON = "$..relasjon[?(@.typeRelasjon=='EKTE')].fnrRelasjon";
    private static final String ROUTINE_PERSDATA = "FS03-FDNUMMER-PERSDATA-O";
    private static final String ROUTINE_PERSRELA = "FS03-FDNUMMER-PERSRELA-O";

    @Autowired
    private TpsStatusQuoService tpsStatusQuoService;

    public Map<String, String> getStatusQuoFraAarsakskode(String endringskode, String environment, String fnr) throws IOException {
        Map<String, String> personStatusQuo = new HashMap<>();
        List<String> feltnavn;

        var endringskoden = Endringskoder.getEndringskodeFraVerdi(endringskode);

        switch (endringskoden) {
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
            feltnavn = Arrays.asList(DATO_DO, STATSBORGER);
            personStatusQuo.putAll(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, feltnavn, environment, fnr));
            break;
        case VIGSEL:
        case SEPERASJON:
        case SKILSMISSE:
        case KORREKSJON_FAMILIEOPPLYSNINGER:
        case DOEDSMELDING:
            feltnavn = Arrays.asList(DATO_DO, STATSBORGER, "sivilstand", "datoSivilstand");
            personStatusQuo.putAll(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, feltnavn, environment, fnr));
            feltnavn = Collections.singletonList(FNR_RELASJON);
            personStatusQuo.putAll(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSRELA, feltnavn, environment, fnr));
            break;
        case ANNULERING_FLYTTING_ADRESSEENDRING:
        case INNFLYTTING_ANNEN_KOMMUNE:
            feltnavn = Arrays.asList(DATO_DO, STATSBORGER, "kommunenr", "datoFlyttet");
            personStatusQuo.putAll(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, feltnavn, environment, fnr));
            break;
        case INNVANDRING:
        case TILDELING_DNUMMER:
        default:
            break;
        }

        return personStatusQuo;
    }
}