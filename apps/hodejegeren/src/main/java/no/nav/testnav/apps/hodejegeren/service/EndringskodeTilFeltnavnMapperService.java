package no.nav.testnav.apps.hodejegeren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EndringskodeTilFeltnavnMapperService {

    public static final String DATO_DO = "datoDo";
    public static final String STATSBORGER = "statsborger"; // lagret i persdata
    public static final String STATSBORGERSKAP = "statsborgerskap"; // lagret i kerninfo
    public static final String NAV_ENHET = "kodeNAVenhet"; // lagret i kerninfo
    public static final String NAV_ENHET_BESKRIVELSE = "kodeNAVenhetBeskr"; // lagret i kerninfo
    public static final String FNR_RELASJON = "$..relasjon[?(@.typeRelasjon=='EKTE')].fnrRelasjon";
    private static final String ROUTINE_PERSDATA = "FS03-FDNUMMER-PERSDATA-O";
    private static final String ROUTINE_PERSRELA = "FS03-FDNUMMER-PERSRELA-O";

    private final TpsStatusQuoService tpsStatusQuoService;

    public Map<String, String> getStatusQuoFraAarsakskode(String endringskode, String environment, String fnr) throws IOException {
        Map<String, String> personStatusQuo = new HashMap<>();
        List<String> feltnavn;

        var endringskoden = Endringskoder.getEndringskodeFraVerdi(endringskode);

        switch (endringskoden) {
            case NAVNEENDRING_FOERSTE, NAVNEENDRING_MELDING, NAVNEENDRING_KORREKSJON, ADRESSEENDRING_UTEN_FLYTTING, ADRESSEKORREKSJON, UTVANDRING, FARSKAP_MEDMORSKAP, ENDRING_STATSBORGERSKAP_BIBEHOLD, ENDRING_FAMILIENUMMER, FOEDSELSNUMMERKORREKSJON, ENDRING_FORELDREANSVAR, ENDRING_OPPHOLDSTILLATELSE, ENDRING_POSTADRESSE_TILLEGGSADRESSE, ENDRING_POSTNUMMER_SKOLE_VALG_GRUNNKRETS, KOMMUNEREGULERING, ADRESSEENDRING_GAB, ENDRING_KORREKSJON_FOEDESTED, ENDRING_DUF_NUMMER, FLYTTING_INNEN_KOMMUNEN, FOEDSELSMELDING, UREGISTRERT_PERSON -> {
                feltnavn = Arrays.asList(DATO_DO, STATSBORGER);
                personStatusQuo.putAll(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, feltnavn, environment, fnr));
            }
            case VIGSEL, SEPERASJON, SKILSMISSE, KORREKSJON_FAMILIEOPPLYSNINGER, DOEDSMELDING -> {
                feltnavn = Arrays.asList(DATO_DO, STATSBORGER, "sivilstand", "datoSivilstand");
                personStatusQuo.putAll(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, feltnavn, environment, fnr));
                feltnavn = Collections.singletonList(FNR_RELASJON);
                personStatusQuo.putAll(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSRELA, feltnavn, environment, fnr));
            }
            case ANNULERING_FLYTTING_ADRESSEENDRING, INNFLYTTING_ANNEN_KOMMUNE -> {
                feltnavn = Arrays.asList(DATO_DO, STATSBORGER, "kommunenr", "datoFlyttet");
                personStatusQuo.putAll(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, feltnavn, environment, fnr));
            }
            default -> {
            }
        }

        return personStatusQuo;
    }
}