package no.nav.registre.aareg.consumer.rs.responses.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Utenlandsopphold {

    @JsonProperty("land")
    private String land;

    @JsonProperty("periode")
    private Map<String, String> periode;
}
