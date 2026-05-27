package no.nav.dolly.synt.aap.onnx;

import lombok.experimental.UtilityClass;
import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.VedtakRequestDto;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@UtilityClass
class AapHistoryFilter {

    private static final List<java.util.function.BiConsumer<AapVedtakDto, Integer>> DATE_SHIFTERS = List.of(
            (vedtak, delta) -> vedtak.setFraDato(addDays(vedtak.getFraDato(), delta)),
            (vedtak, delta) -> vedtak.setTilDato(addDays(vedtak.getTilDato(), delta)),
            (vedtak, delta) -> vedtak.setOpprTilDato(addDays(vedtak.getOpprTilDato(), delta)),
            (vedtak, delta) -> vedtak.setJustertFra(addDays(vedtak.getJustertFra(), delta)),
            (vedtak, delta) -> vedtak.setVedtakDato(addDays(vedtak.getVedtakDato(), delta)),
            (vedtak, delta) -> vedtak.setDatoMottatt(addDays(vedtak.getDatoMottatt(), delta))
    );

    static List<VedtakRequestDto> removeIllogicalRequests(List<VedtakRequestDto> requests) {

        var filtered = new ArrayList<VedtakRequestDto>();
        VedtakRequestDto previous = null;
        for (var request : requests) {
            if (nonNull(previous) && isStansWithJa(previous) && isEndringOrStans(request)) {
                continue;
            }
            filtered.add(request);
            previous = request;
        }
        return filtered;

    }

    static List<AapVedtakDto> postprocessForUseInHistory(List<AapVedtakDto> vedtak) {

        var shifted = updateVedtakDates(vedtak);
        return removeVedtakAfterAvbrudd(shifted);

    }

    private static List<AapVedtakDto> updateVedtakDates(List<AapVedtakDto> vedtaksliste) {

        AapVedtakDto previous = null;
        LocalDate previousStart = null;
        LocalDate previousEnd = null;
        for (var vedtak : vedtaksliste) {

            var start = parseDate(vedtak.getFraDato());
            var end = parseDate(vedtak.getTilDato());
            if (nonNull(previous) && !isType(vedtak.getVedtaktype(), "O") && nonNull(start)) {
                var delta = neededShift(start, previousStart, previousEnd);
                if (delta != 0) {
                    shiftDates(vedtak, delta);
                    start = parseDate(vedtak.getFraDato());
                    end = parseDate(vedtak.getTilDato());
                }
            }
            if (!isType(vedtak.getUtfall(), "NEI")) {
                previous = vedtak;
                previousStart = start;
                previousEnd = end;
            }

        }
        return vedtaksliste;

    }

    private static List<AapVedtakDto> removeVedtakAfterAvbrudd(List<AapVedtakDto> vedtaksliste) {

        var filtered = new ArrayList<AapVedtakDto>();
        AapVedtakDto previous = null;
        for (var vedtak : vedtaksliste) {

            if (hasText(vedtak.getAvbruddskode()) && isOlderThan21Days(vedtak.getFraDato())) {
                vedtak.setAvbruddskode("");
            }
            if (nonNull(previous) && hasText(previous.getAvbruddskode()) && !isType(vedtak.getVedtaktype(), "O")) {
                continue;
            }
            filtered.add(vedtak);
            previous = vedtak;

        }
        return filtered;

    }

    private static int neededShift(LocalDate start, LocalDate previousStart, LocalDate previousEnd) {

        if (start == null || previousStart == null) {
            return 0;
        }
        if (previousEnd == null) {
            if (!start.isAfter(previousStart)) {
                return (int) java.time.temporal.ChronoUnit.DAYS.between(start, previousStart.plusDays(1));
            }
            return 0;
        }
        if (!start.isAfter(previousEnd)) {
            return (int) ChronoUnit.DAYS.between(start, previousEnd.plusDays(1));
        }
        return 0;

    }

    private static void shiftDates(AapVedtakDto vedtak, int delta) {
        for (var shifter : DATE_SHIFTERS) {
            shifter.accept(vedtak, delta);
        }
    }

    private static boolean isOlderThan21Days(String fraDato) {
        var parsed = parseDate(fraDato);
        return parsed != null && parsed.isBefore(LocalDate.now().minusDays(21));
    }

    private static String addDays(String value, int days) {
        var date = parseDate(value);
        if (date == null) {
            return value;
        }
        return date.plusDays(days).toString();
    }

    private static LocalDate parseDate(String value) {
        if (!hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static boolean isStansWithJa(VedtakRequestDto request) {
        return isType(request.getVedtakTypeKode(), "S") && isType(request.getUtfall(), "JA");
    }

    private static boolean isEndringOrStans(VedtakRequestDto request) {
        return isType(request.getVedtakTypeKode(), "E") || isType(request.getVedtakTypeKode(), "S");
    }

    private static boolean isType(String value, String expected) {
        return expected.equalsIgnoreCase(value);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

