package no.nav.registre.tss.utils;

import java.util.ArrayList;

import no.nav.registre.tss.consumer.rs.responses.Response110;
import no.nav.registre.tss.consumer.rs.responses.Response111;
import no.nav.registre.tss.consumer.rs.responses.Response125;
import no.nav.registre.tss.consumer.rs.responses.Response910;

public class Response910Util {

    private static int POS_AFTER_HEADER = 531;
    private static int MESSAGE_LENGTH = 203;

    public static Response910 parseResponse(String response) {
        int currentPos = POS_AFTER_HEADER;
        int numberOfMessages = 0;

        Response910 response910 = Response910.builder()
                .response110(new ArrayList<>())
                .response111(new ArrayList<>())
                .response125(new ArrayList<>())
                .build();

        while (currentPos < response.length()) {
            numberOfMessages++;
            String idKode = response.substring(currentPos, currentPos += 3);

            if ("110".equals(idKode)) {
                response910.getResponse110().add(Response110.builder()
                        .idKode(idKode.trim())
                        .idOff(response.substring(currentPos, currentPos += 11).trim())
                        .kodeIdenttype(response.substring(currentPos, currentPos += 4).trim())
                        .kodeSamhType(response.substring(currentPos, currentPos += 4).trim())
                        .BeskrSamhType(response.substring(currentPos, currentPos += 30).trim())
                        .datoSamhType(response.substring(currentPos, currentPos += 8).trim())
                        .datoSamhTom(response.substring(currentPos, currentPos += 8).trim())
                        .navn(response.substring(currentPos, currentPos += 40).trim())
                        .kodeSpraak(response.substring(currentPos, currentPos += 4).trim())
                        .etatsmerke(response.substring(currentPos, currentPos += 1).trim())
                        .utbetalingssperre(response.substring(currentPos, currentPos += 1).trim())
                        .kodeKontrint(response.substring(currentPos, currentPos += 4).trim())
                        .beskrKontrint(response.substring(currentPos, currentPos += 30).trim())
                        .kodeStatus(response.substring(currentPos, currentPos += 4).trim())
                        .BeskrStatus(response.substring(currentPos, currentPos += 26).trim())
                        .oppdater(response.substring(currentPos, currentPos += 1).trim())
                        .kilde(response.substring(currentPos, currentPos += 4).trim())
                        .brukerId(response.substring(currentPos, currentPos += 8).trim())
                        .tidReg(response.substring(currentPos, currentPos += 12).trim())
                        .build());
            } else if ("111".equals(idKode)) {
                response910.getResponse111().add(Response111.builder()
                        .idKode(idKode.trim())
                        .idAlternativ(response.substring(currentPos, currentPos += 11).trim())
                        .kodeAltIdenttype(response.substring(currentPos, currentPos += 4).trim())
                        .beskrAltIdenttype(response.substring(currentPos, currentPos += 40).trim())
                        .datoIdentFom(response.substring(currentPos, currentPos += 8).trim())
                        .datoIdentTom(response.substring(currentPos, currentPos += 8).trim())
                        .gyldigIdent(response.substring(currentPos, currentPos += 1).trim())
                        .kilde(response.substring(currentPos, currentPos += 4).trim())
                        .brukerid(response.substring(currentPos, currentPos += 8).trim())
                        .tidReg(response.substring(currentPos, currentPos += 12).trim())
                        .build());
            } else if ("125".equals(idKode)) {
                response910.getResponse125().add(Response125.builder()
                        .idKode(idKode.trim())
                        .avdelingsnr(response.substring(currentPos, currentPos += 2).trim())
                        .avdelingsnavn(response.substring(currentPos, currentPos += 40).trim())
                        .typeAvdeling(response.substring(currentPos, currentPos += 4).trim())
                        .beskrAvdelingstype(response.substring(currentPos, currentPos += 40).trim())
                        .datoAvdelingFrom(response.substring(currentPos, currentPos += 8).trim())
                        .datoAvdelingTom(response.substring(currentPos, currentPos += 8).trim())
                        .gyldigAvdeling(response.substring(currentPos, currentPos += 1).trim())
                        .idTSSEkstern(response.substring(currentPos, currentPos += 11).trim())
                        .avdOffnr(response.substring(currentPos, currentPos += 11).trim())
                        .kilde(response.substring(currentPos, currentPos += 4).trim())
                        .brukerid(response.substring(currentPos, currentPos += 8).trim())
                        .tidReg(response.substring(currentPos, currentPos += 12).trim())
                        .build());
            }

            currentPos = (numberOfMessages * MESSAGE_LENGTH) + POS_AFTER_HEADER;
        }

        return response910;
    }
}
