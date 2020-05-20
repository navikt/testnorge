package no.nav.registre.ereg.domain;

import java.util.Optional;

abstract class Flatfil {

    String trimAndEmptyToNull(String value) {
        if (value.trim().length() == 0) {
            return null;
        }
        return value.trim();
    }

    String getValueFromRecord(String record, int start, int slutt) {
        if (record.length() < start + 1) {
            return null;
        }
        return trimAndEmptyToNull(record.substring(start, Math.min(record.length(), slutt)));
    }

    String getValueFromOptionalRecord(Optional<String> record, int start, int slutt) {
        return record.map(s -> getValueFromRecord(s, start, slutt)).orElse(null);
    }

}
