package no.nav.registre.syntrest.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Validation {
    enum Meldegrupper {ATTF, DAGP, INDI, ARBS, FY}

    private final List<String> endringskoder = new ArrayList<>(Arrays.asList("0110", "0211", "0610", "0710", "1010", "1110", "1410", "1810",
            "2410", "2510", "2610", "2810", "2910", "3210", "3410", "3810", "3910", "4010", "4110", "4310",
            "4410", "4710", "4910", "5110", "5610", "9110"));


    private final List<String> NavEndringskoder = new ArrayList<>(Arrays.asList("Z010", "Z510", "Z310", "ZM10", "Z610", "ZV10", "ZD10", "1810",
            "Z810"));

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

    public boolean validateEia(List<Map<String, String>> request) {
        for (Map<String, String> map : request) {
            for (String key : map.keySet()) {
                if (key.equals("fnr")) {
                    String[] list = {map.get(key)};
                    System.out.println(list);
                    boolean isValid = validateFnrs(list);
                    if (isValid == false) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean validateMeldegruppe(String meldegruppe) {
        for (Meldegrupper m : Meldegrupper.values()) {
            if (meldegruppe.equals(m.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean validateEndringskode(String endringskode) {
        for (String s : endringskoder) {
            if (endringskode.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean validateNavEndringskode(String endringskode) {
        for (String s : NavEndringskoder) {
            if (endringskode.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getEndringskoder() {
        return endringskoder;
    }

    public List<String> getNavEndringskoder() {
        return NavEndringskoder;
    }
}