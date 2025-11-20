package no.nav.testnav.pdllagreservice.listener;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@UtilityClass
public class PdlAdresseMapper {

    private static final String NUMMER = "nummer";
    private static final String VEGADRESSE = "vegadresse";

    public static void replaceNumberWithString(Map<String, Object> data) {

        val vegadresse = (Map<String, Object>) data.getOrDefault(VEGADRESSE, null);
        if (nonNull(vegadresse)) {
            // auto-completion greier ikke håndtere Tall og må ha text :(
            //https://stackoverflow.com/questions/53903142/logstash-cant-parse-type-completion-to-numbers
            val nummer = vegadresse.get(NUMMER);
            if (nonNull(nummer)) {
                vegadresse.put(NUMMER, nummer.toString());
            }

            val bydel = (Map<String, Object>) vegadresse.get("bydel");
            if (nonNull(bydel)) {
                val bydelsnummer = bydel.get("bydelsnummer");
                if (nonNull(bydelsnummer)) {
                    bydel.put("bydelsnummer", String.format("%06d", Integer.parseInt(bydelsnummer.toString())));
                }
            }
        }
    }

    public static void createCompletionContexts(Map<String, Object> data) {

        // opensearch sin autocomplete context  støtter bare OR operasjon dersom man legger til flere,
        // så for å filtrere på adressenavn og nummer for å finne bokstaver så må det lages ett sammensatt felt.
        // det er heller ikke ut til at autocomplete context tar høyde for lowercase filter i fra analyser so vi gjør toLowercase her.
        // (alternativt kunne man filtrert  resultatene, men OR operasjonen betyr mer data  for elastic å lese gjennom så  går for ekstra felt istedet.)

        val customFields = new HashMap<String, Object>();
        data.put("custom_fields", customFields);

        val fritekstMap = new HashMap<String, String>();
        customFields.put("fritekst", fritekstMap);

        val vegadresse = (Map<String, Object>) data.getOrDefault(VEGADRESSE, null);
        val matrikkeladresse = (Map<String, Object>) data.getOrDefault("matrikkeladresse", null);

        if (nonNull(vegadresse)) {
            addFieldsForVegadresse(customFields, fritekstMap, vegadresse);

        } else if (nonNull(matrikkeladresse)) {
            addFieldsForMatrikkeladresse(fritekstMap, matrikkeladresse);
        }
    }

    private static void addFieldsForMatrikkeladresse(Map<String, String> fritekstMap, Map<String, Object> matrikkeladresse) {

        val enhet = (Map<String, Object>) matrikkeladresse.getOrDefault("matrikkelenhet", null);

        if (nonNull(enhet)) {
            val matrikkelnummer = (Map<String, Object>) matrikkeladresse.getOrDefault("matrikkelnummer", null);

            if (nonNull(matrikkelnummer)) {
                val tilleggsnavn = emptyStringIfNull((String) matrikkelnummer.get("adressetilleggsnavn"));
                val gardsnummer = emptyStringIfNull((String) matrikkelnummer.get("gardsnummer"));
                val bruksnummer = emptyStringIfNull((String) matrikkelnummer.get("bruksnummer"));

                val navn = tilleggsnavn.isEmpty() ? "" : tilleggsnavn + ", ";
                var fritekstData = navn + gardsnummer + "/" + bruksnummer;

                val postnummeromrade = (Map<String, Object>) matrikkeladresse.get("postnummeromraade");
                if (nonNull(postnummeromrade)) {
                    val poststed = emptyStringIfNull((String) postnummeromrade.get("poststed"));
                    val postnummer = emptyStringIfNull((String) postnummeromrade.get("postnummer"));
                    fritekstData = fritekstData.trim() + ", " + postnummer + " " + poststed;
                }


                fritekstMap.put("matrikkeladresse", fritekstData.trim());
            }
        }
    }

    private static void addFieldsForVegadresse(Map<String, Object> customFields, Map<String, String> fritekstMap, Map<String, Object> vegadresse) {

        val veg = (Map<String, Object>) vegadresse.getOrDefault("veg", null);
        if (nonNull(veg)) {
            val bokstav = emptyStringIfNull((String) vegadresse.get("bokstav"));
            val nummer = emptyStringIfNull((String) vegadresse.get(NUMMER));
            val adressenavn = emptyStringIfNull((String) veg.get("adressenavn"));
            val navnNummerContext = adressenavn + "_" + nummer;

            var fritekstData = adressenavn + " " + nummer + " " + bokstav;

            val postnummeromrade = (Map<String, Object>) vegadresse.get("postnummeromraade");
            if (nonNull(postnummeromrade)) {
                val poststed = emptyStringIfNull((String) postnummeromrade.get("poststed"));
                val postnummer = emptyStringIfNull((String) postnummeromrade.get("postnummer"));
                fritekstData = fritekstData.trim() + ", " + postnummer + " " + poststed;
            }

            fritekstMap.put(VEGADRESSE, fritekstData.trim());
            customFields.put("context_adressenavn_nummer", navnNummerContext.toLowerCase());
        }
    }

    private static String emptyStringIfNull(String value) {
        return value == null ? "" : value;
    }

}
