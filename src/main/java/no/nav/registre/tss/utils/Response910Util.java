package no.nav.registre.tss.utils;

public class Response910Util {

    public static Response910 parseResponse(String response) {
        int currentPos = 0;

        return Response910.builder()
                .idKode(response.substring(currentPos += 3, currentPos))
                .idOff(response.substring(currentPos += 11, currentPos))
                .kodeIdenttype(response.substring(currentPos += 4, currentPos))
                .kodeSamhType(response.substring(currentPos += 4, currentPos))
                .BeskrSamhType(response.substring(currentPos += 30, currentPos))
                .datoSamhType(response.substring(currentPos += 8, currentPos))
                .datoSamhTom(response.substring(currentPos += 8, currentPos))
                .navn(response.substring(currentPos += 40, currentPos))
                .kodeSpraak(response.substring(currentPos += 4, currentPos))
                .etatsmerke(response.substring(currentPos += 1, currentPos))
                .utbetalingssperre(response.substring(currentPos += 1, currentPos))
                .kodeKontrint(response.substring(currentPos += 4, currentPos))
                .beskrKontrint(response.substring(currentPos += 30, currentPos))
                .kodeStatus(response.substring(currentPos += 4, currentPos))
                .BeskrStatus(response.substring(currentPos += 26, currentPos))
                .oppdater(response.substring(currentPos += 1, currentPos))
                .kilde(response.substring(currentPos += 4, currentPos))
                .brukerId(response.substring(currentPos += 8, currentPos))
                .tidReg(response.substring(currentPos += 12, currentPos))
                .build();
    }
}
