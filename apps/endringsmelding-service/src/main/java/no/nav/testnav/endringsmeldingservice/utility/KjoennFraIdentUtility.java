package no.nav.testnav.endringsmeldingservice.utility;

import lombok.experimental.UtilityClass;

import static java.lang.Integer.parseInt;

@UtilityClass
public class KjoennFraIdentUtility {

    public String get(String ident){

        int kjoennNummer = parseInt(ident.substring(8,9));
        return kjoennNummer % 2 == 0 ? "K" : "M";
    }
}