package no.nav.registre.tss.utils;

public class Response910Util {

    private static int HEADER_LENGTH = 531;

    public static Response910 parseResponse(String response) {
        int currentPos = HEADER_LENGTH;

        return Response910.builder()
                .idKode(response.substring(currentPos, currentPos += 3))
                .idOff(response.substring(currentPos, currentPos += 11))
                .kodeIdenttype(response.substring(currentPos, currentPos += 4))
                .kodeSamhType(response.substring(currentPos, currentPos += 4))
                .BeskrSamhType(response.substring(currentPos, currentPos += 30))
                .datoSamhType(response.substring(currentPos, currentPos += 8))
                .datoSamhTom(response.substring(currentPos, currentPos += 8))
                .navn(response.substring(currentPos, currentPos += 40))
                .kodeSpraak(response.substring(currentPos, currentPos += 4))
                .etatsmerke(response.substring(currentPos, currentPos += 1))
                .utbetalingssperre(response.substring(currentPos, currentPos += 1))
                .kodeKontrint(response.substring(currentPos, currentPos += 4))
                .beskrKontrint(response.substring(currentPos, currentPos += 30))
                .kodeStatus(response.substring(currentPos, currentPos += 4))
                .BeskrStatus(response.substring(currentPos, currentPos += 26))
                .oppdater(response.substring(currentPos, currentPos += 1))
                .kilde(response.substring(currentPos, currentPos += 4))
                .brukerId(response.substring(currentPos, currentPos += 8))
                .tidReg(response.substring(currentPos, currentPos + 12))
                .build();
    }
}
