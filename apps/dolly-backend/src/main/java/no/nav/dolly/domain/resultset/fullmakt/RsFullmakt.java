package no.nav.dolly.domain.resultset.fullmakt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsFullmakt {

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime gyldigFraOgMed;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime gyldigTilOgMed;
    private String fullmektig;
    private String fullmaktsgiver;
    private List<Omraade> omraade;

    public List<Omraade> getOmraade() {
        if (isNull(omraade)) {
            omraade = new ArrayList<>();
        }
        return omraade;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Omraade {
        private String tema;
        private List<String> handling;

        public List<String> getHandling() {
            if (isNull(handling)) {
                handling = new ArrayList<>();
            }
            return handling;
        }
    }
}