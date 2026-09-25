package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister;

import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public record SkjermingsregisterResourceStatus(
        boolean empty,
        boolean owned,
        boolean active,
        boolean terminated,
        boolean expectedDataPresent
) {

    public static SkjermingsregisterResourceStatus from(
            JsonNode response,
            SkjermingsregisterRequest expectedRequest,
            LocalDateTime referenceTime
    ) {
        var entry = response.isArray() && !response.isEmpty() ? response.get(0) : response;
        if (!entry.isObject()) {
            throw new IllegalStateException(
                    "Skjermingsregister-oppslaget returnerte ugyldig respons.");
        }
        var from = parseDate(entry.path("skjermetFra"));
        var to = parseDate(entry.path("skjermetTil"));
        var owned = SkjermingsregisterTestData.FIRST_NAME.equals(
                entry.path("fornavn").asString())
                && SkjermingsregisterTestData.LAST_NAME.equals(
                entry.path("etternavn").asString());
        var expectedDataPresent = owned
                && expectedRequest.personident().equals(entry.path("personident").asString())
                && expectedRequest.skjermetFra().equals(from)
                && expectedRequest.skjermetTil().equals(to);
        return new SkjermingsregisterResourceStatus(
                false,
                owned,
                to == null || to.isAfter(referenceTime),
                to != null && !to.isAfter(referenceTime),
                expectedDataPresent);
    }

    public static SkjermingsregisterResourceStatus emptyStatus() {
        return new SkjermingsregisterResourceStatus(true, false, false, false, false);
    }

    private static LocalDateTime parseDate(JsonNode value) {
        var text = value.asString();
        if (text.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException _) {
            return OffsetDateTime.parse(text).toLocalDateTime();
        }
    }
}
