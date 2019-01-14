package no.nav.registre.orkestratoren.consumer.rs.requests;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendToTpsRequest {
    @JsonProperty("environment")
    private String environment;
    @JsonProperty("ids")
    private List<Long> ids;
}
