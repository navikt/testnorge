package no.nav.testnav.libs.slack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ButtonAction implements Action {
    String type = "button";
    String text;
    String url;
    String style = "primary";
}
