package no.nav.registre.tss.utils;

import java.util.List;

import no.nav.registre.tss.consumer.rs.responses.TssSyntMessage;

public class RutineUtil {

    public static final int MELDINGSLENGDE = 203;

    public static String opprettFlatfil(List<TssSyntMessage> rutiner) {
        StringBuilder flatfil = new StringBuilder();
        for (TssSyntMessage rutine : rutiner) {
            String idKode = rutine.getIdKode();

            switch (idKode) {
            case "110":
                flatfil.append(Rutine110Util.opprett110Rutine(rutine));
                break;
            case "111":
                flatfil.append(Rutine111Util.opprett111Rutine(rutine));
                break;
            case "170":
                flatfil.append(Rutine170Util.opprett170Rutine(rutine));
                break;
            case "175":
                flatfil.append(Rutine175Util.opprett175Rutine(rutine));
                break;
            default:
                throw new RuntimeException("Ukjent idKode");
            }
        }

        return flatfil.toString();
    }

}
