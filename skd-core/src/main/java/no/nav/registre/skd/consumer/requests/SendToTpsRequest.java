package no.nav.registre.skd.consumer.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
