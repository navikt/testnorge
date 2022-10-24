package no.nav.testnav.apps.personexportapi.consumer.dto;

import static java.util.Objects.nonNull;

import org.bouncycastle.asn1.eac.BidirectionalMap;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HusbokstavEncoder {

    private static BidirectionalMap encodingMap;

    static {
        encodingMap = new BidirectionalMap();
        encodingMap.put("A", "9901");
        encodingMap.put("B", "9902");
        encodingMap.put("C", "9903");
        encodingMap.put("D", "9904");
        encodingMap.put("E", "9905");
        encodingMap.put("F", "9906");
        encodingMap.put("G", "9907");
        encodingMap.put("H", "9908");
        encodingMap.put("I", "9909");
        encodingMap.put("J", "9910");
        encodingMap.put("K", "9911");
        encodingMap.put("L", "9912");
        encodingMap.put("M", "9913");
        encodingMap.put("N", "9914");
        encodingMap.put("O", "9915");
        encodingMap.put("P", "9916");
        encodingMap.put("Q", "9917");
        encodingMap.put("R", "9918");
        encodingMap.put("S", "9919");
        encodingMap.put("T", "9920");
        encodingMap.put("U", "9921");
        encodingMap.put("V", "9922");
        encodingMap.put("W", "9923");
        encodingMap.put("X", "9924");
        encodingMap.put("Y", "9925");
        encodingMap.put("Z", "9926");
        encodingMap.put("Æ", "9927");
        encodingMap.put("Ø", "9928");
        encodingMap.put("Å", "9929");
        encodingMap.put("Á", "9930");
    }

    public static String encode(String husbokstav) {
        return (String) encodingMap.get(husbokstav);
    }

    public static String decode(String skdHusnummerCode) {
        return nonNull(skdHusnummerCode) ? (String) encodingMap.getReverse(skdHusnummerCode) : null;
    }
}
