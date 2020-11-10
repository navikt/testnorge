package no.nav.registre.testnorge.identservice.testdata.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TpsRequestParameterCreator {
    
    public static Map<String, Object> opprettParametereForM201TpsRequest(String ident, String aksjonskode) {
        Map<String, Object> tpsRequestParameters = new HashMap<>();
        tpsRequestParameters.put("serviceRutinenavn", "FS03-FDLISTER-DISKNAVN-M-TESTDATA");
        tpsRequestParameters.put("fnr", ident);
        tpsRequestParameters.put("antallFnr", "1");
        tpsRequestParameters.put("aksjonsKode", aksjonskode);
        return tpsRequestParameters;
    }
}
