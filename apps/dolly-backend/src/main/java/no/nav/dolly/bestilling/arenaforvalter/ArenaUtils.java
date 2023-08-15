package no.nav.dolly.bestilling.arenaforvalter;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@UtilityClass
public class ArenaUtils {

    public static final String OPPRETTET = "Oppretting: ";
    public static final String AVSLAG = "Avslag: ";
    public static final String INAKTIVERT = "Inaktivert: ";
    public static final String STATUS_FMT = "%s:%s";
    public static final String IDENT = "ident";
    public static final String MILJOE = "miljoe";
    public static final String STANSET = "Stanset forrige vedtak: ";

    public static LocalDate toDate(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.toLocalDate() : null;
    }
}
