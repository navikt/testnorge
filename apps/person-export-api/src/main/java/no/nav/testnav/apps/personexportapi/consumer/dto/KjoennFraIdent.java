package no.nav.testnav.apps.personexportapi.consumer.dto;

import static java.lang.Integer.parseInt;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KjoennFraIdent {

    public KjoennType getKjoenn(String ident){

        int kjoennNummer = parseInt(ident.substring(8,9));
        return kjoennNummer % 2 == 0 ? KjoennType.K : KjoennType.M;
    }
}