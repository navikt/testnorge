package no.nav.testnav.apps.importfratpsfservice.mapper;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class HusbokstavDekoder {

    private static Map<String, String> encodingMap;

    static {
        encodingMap = new HashMap<>();
        encodingMap.put("9901", "A");
        encodingMap.put("9902", "B");
        encodingMap.put("9903", "C");
        encodingMap.put("9904", "D");
        encodingMap.put("9905", "E");
        encodingMap.put("9906", "F");
        encodingMap.put("9907", "G");
        encodingMap.put("9908", "H");
        encodingMap.put("9909", "I");
        encodingMap.put("9910", "J");
        encodingMap.put("9911", "K");
        encodingMap.put("9912", "L");
        encodingMap.put("9913", "M");
        encodingMap.put("9914", "N");
        encodingMap.put("9915", "O");
        encodingMap.put("9916", "P");
        encodingMap.put("9917", "Q");
        encodingMap.put("9918", "R");
        encodingMap.put("9919", "S");
        encodingMap.put("9920", "T");
        encodingMap.put("9921", "U");
        encodingMap.put("9922", "V");
        encodingMap.put("9923", "W");
        encodingMap.put("9924", "X");
        encodingMap.put("9925", "Y");
        encodingMap.put("9926", "Z");
        encodingMap.put("9927", "Æ");
        encodingMap.put("9928", "Ø");
        encodingMap.put("9929", "Å");
        encodingMap.put("9930", "Á");
    }

    public static String getHusbokstav(String skdKode) {
        return encodingMap.get(skdKode);
    }
}
