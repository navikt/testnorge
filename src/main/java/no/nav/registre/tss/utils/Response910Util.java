package no.nav.registre.tss.utils;

public class Response910Util {

    private static int HEADER_LENGTH = 531;

    public static Response910 parseResponse(String response) {
        int currentPos = HEADER_LENGTH;

        return Response910.builder()
                .idKode(response.substring(currentPos, currentPos += 3).trim())
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
                .tidReg(response.substring(currentPos, currentPos + 12).trim())
                .build();
    }
}
