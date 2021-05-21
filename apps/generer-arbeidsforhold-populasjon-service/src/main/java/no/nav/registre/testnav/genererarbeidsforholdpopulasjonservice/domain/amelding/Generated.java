package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

abstract class Generated {

    Float nullToEmpty(Float value) {
        return value == null ? 0f : value;
    }

    String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    String emptyToNull(String value) {
        return value == null ? null : value.equals("") ? null : value;
    }

    Float emptyToNull(Float value) {
        return value == 0f ? null : value;
    }

    String formatKaldenermaand(LocalDate value) {
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    String format(LocalDate value) {
        if (value == null) {
            return "";
        }
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    LocalDate format(String value) {
        return Strings.isBlank(value) ? null : LocalDate.parse(value);
    }


    no.nav.registre.testnorge.libs.dto.syntrest.v1.AvvikDTO toSyntAvvik(List<Avvik> list) {
        return list == null ? null : list.stream().findFirst().map(Avvik::toSynt).orElse(null);
    }

}
