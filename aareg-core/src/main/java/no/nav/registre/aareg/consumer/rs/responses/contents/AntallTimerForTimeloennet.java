package no.nav.registre.aareg.consumer.rs.responses.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AntallTimerForTimeloennet {

    @JsonProperty("antallTimer")
    private int antallTimer;

    @JsonProperty("periode")
    private Map<String, String> periode;
}
