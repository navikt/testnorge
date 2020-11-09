package no.nav.registre.testnorge.personexportapi.consumer.dto;

import static java.lang.Integer.parseInt;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KjoennFraIdent {

    public enum KjoennType {K, M, U }

    public KjoennType getKjoenn(String ident){

        int kjoennNummer = parseInt(ident.substring(8,9));
        return kjoennNummer % 2 == 0 ? KjoennType.K : KjoennType.M;
    }
}