package no.nav.registre.sdForvalter.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SlackChannelResponse {

    private Boolean ok;
    @JsonProperty(value = "response_metadata")
    private Map<String, Object> responseMetadata;
    private List<Map<String, Object>> channels;

}
