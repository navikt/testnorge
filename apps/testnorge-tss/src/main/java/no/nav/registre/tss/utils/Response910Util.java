package no.nav.registre.tss.utils;

import java.util.ArrayList;

import static no.nav.registre.tss.utils.RutineUtil.MELDINGSLENGDE;
import no.nav.registre.tss.consumer.rs.response.Response110;
import no.nav.registre.tss.consumer.rs.response.Response111;
import no.nav.registre.tss.consumer.rs.response.Response125;
import no.nav.registre.tss.consumer.rs.response.Response910;

public class Response910Util {

    private static final int POS_AFTER_HEADER = 531;

    public static Response910 parseResponse(String response) {
        CurrentPosition currentPosition = new CurrentPosition(POS_AFTER_HEADER);
        int numberOfMessages = 0;

        Response910 response910 = Response910.builder()
                .response110(new ArrayList<>())
                .response111(new ArrayList<>())
                .response125(new ArrayList<>())
                .build();

        while (currentPosition.getIndex() < response.length()) {
            numberOfMessages++;
            String idKode = getFieldAndUpdateCurrentPosition(response, currentPosition, 3);

            if ("110".equals(idKode)) {
                response910.getResponse110().add(Response110.builder()
                        .idKode(idKode.trim())
                        .idOff(getFieldAndUpdateCurrentPosition(response, currentPosition, 11).trim())
                        .kodeIdenttype(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .kodeSamhType(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .beskrSamhType(getFieldAndUpdateCurrentPosition(response, currentPosition, 30).trim())
                        .datoSamhType(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .datoSamhTom(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .navn(getFieldAndUpdateCurrentPosition(response, currentPosition, 40).trim())
                        .kodeSpraak(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .etatsmerke(getFieldAndUpdateCurrentPosition(response, currentPosition, 1).trim())
                        .utbetalingssperre(getFieldAndUpdateCurrentPosition(response, currentPosition, 1).trim())
                        .kodeKontrint(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .beskrKontrint(getFieldAndUpdateCurrentPosition(response, currentPosition, 30).trim())
                        .kodeStatus(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .beskrStatus(getFieldAndUpdateCurrentPosition(response, currentPosition, 26).trim())
                        .oppdater(getFieldAndUpdateCurrentPosition(response, currentPosition, 1).trim())
                        .kilde(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .brukerId(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .tidReg(getFieldAndUpdateCurrentPosition(response, currentPosition, 12).trim())
                        .build());
            } else if ("111".equals(idKode)) {
                response910.getResponse111().add(Response111.builder()
                        .idKode(idKode.trim())
                        .idAlternativ(getFieldAndUpdateCurrentPosition(response, currentPosition, 11).trim())
                        .kodeAltIdenttype(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .beskrAltIdenttype(getFieldAndUpdateCurrentPosition(response, currentPosition, 40).trim())
                        .datoIdentFom(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .datoIdentTom(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .gyldigIdent(getFieldAndUpdateCurrentPosition(response, currentPosition, 1).trim())
                        .kilde(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .brukerid(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .tidReg(getFieldAndUpdateCurrentPosition(response, currentPosition, 12).trim())
                        .build());
            } else if ("125".equals(idKode)) {
                response910.getResponse125().add(Response125.builder()
                        .idKode(idKode.trim())
                        .avdelingsnr(getFieldAndUpdateCurrentPosition(response, currentPosition, 2).trim())
                        .avdelingsnavn(getFieldAndUpdateCurrentPosition(response, currentPosition, 40).trim())
                        .typeAvdeling(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .beskrAvdelingstype(getFieldAndUpdateCurrentPosition(response, currentPosition, 40).trim())
                        .datoAvdelingFrom(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .datoAvdelingTom(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .gyldigAvdeling(getFieldAndUpdateCurrentPosition(response, currentPosition, 1).trim())
                        .idTSSEkstern(getFieldAndUpdateCurrentPosition(response, currentPosition, 11).trim())
                        .avdOffnr(getFieldAndUpdateCurrentPosition(response, currentPosition, 11).trim())
                        .kilde(getFieldAndUpdateCurrentPosition(response, currentPosition, 4).trim())
                        .brukerid(getFieldAndUpdateCurrentPosition(response, currentPosition, 8).trim())
                        .tidReg(getFieldAndUpdateCurrentPosition(response, currentPosition, 12).trim())
                        .build());
            }

            currentPosition.setIndex((numberOfMessages * MELDINGSLENGDE) + POS_AFTER_HEADER);
        }

        return response910;
    }

    private static String getFieldAndUpdateCurrentPosition(String response, CurrentPosition currentPosition, int length) {
        int position = currentPosition.getIndex();
        String field = response.substring(position, position + length);
        currentPosition.setIndex(position + length);
        return field;
    }
}
