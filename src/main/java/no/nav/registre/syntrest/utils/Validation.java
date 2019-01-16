package no.nav.registre.syntrest.utils;

import java.util.List;
import java.util.Map;

public class Validation {

    public Validation() {
    }

    public boolean validateFnrs(String[] fnrs) {
        for (String fnr : fnrs) {
            if (fnr.length() != 11 || !(fnr instanceof String)) {
                return false;
            }
        }
        return true;
    }

    public boolean validateEia(List<Map<String, String>> request){
        for (Map<String, String> map : request){
            for (String key : map.keySet()){
                if (key.equals("fnr")){
                    String[] list = {map.get(key)};
                    System.out.println(list);
                    boolean isValid = validateFnrs(list);
                    if (isValid == false){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}