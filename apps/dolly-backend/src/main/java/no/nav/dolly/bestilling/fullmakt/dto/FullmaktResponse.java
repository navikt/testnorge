package no.nav.dolly.bestilling.fullmakt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullmaktResponse {

    private HttpStatus status;
    private String melding;
    private List<Fullmakt> fullmakt;

    public List<Fullmakt> getFullmakt() {
        if (isNull(fullmakt)) {
            fullmakt = new ArrayList<>();
        }
        return fullmakt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fullmakt {

        private Integer fullmaktId;
        private LocalDateTime registrert;
        private String registrertAv;
        private LocalDateTime endret;
        private String endretAv;
        private Boolean opphoert;
        private String fullmaktsgiver;
        private String fullmektig;
        private List<Omraade> omraade;
        private LocalDate gyldigFraOgMed;
        private LocalDate gyldigTilOgMed;
        private String fullmaktUuid;
        private String opplysningsId;
        private Integer endringsId;
        private String status;
        private String kilde;
        private String fullmaktsgiverNavn;
        private String fullmektigsNavn;

        public List<Omraade> getOmraade() {
            if (isNull(omraade)) {
                omraade = new ArrayList<>();
            }
            return omraade;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Omraade {
        private String tema;
        private List<String> handling;
    }
}
