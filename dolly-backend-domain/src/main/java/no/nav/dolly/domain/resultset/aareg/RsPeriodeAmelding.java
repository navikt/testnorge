package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPeriodeAmelding {

    @Schema(description = "Dato fra-og-med",
            type = "LocalDateTime",
            required = true)
    private LocalDateTime fom;

    @Schema(description = "Dato til-og-med",
            type = "LocalDateTime")
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
