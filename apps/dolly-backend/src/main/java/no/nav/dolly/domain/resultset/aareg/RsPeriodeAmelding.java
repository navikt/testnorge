package no.nav.dolly.domain.resultset.aareg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPeriodeAmelding {

    @Schema(description = "Dato fra-og-med",
            type = "LocalDateTime",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime fom;

    @Schema(description = "Dato til-og-med",
            type = "LocalDateTime")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime tom;

    @Schema(description = "Samlet liste over periode",
            type = "List")
    private List<String> periode;

    public List<String> getPeriode() {
        if (isNull(periode)) {
            periode = new ArrayList<>();
        }
        return periode;
    }
}
