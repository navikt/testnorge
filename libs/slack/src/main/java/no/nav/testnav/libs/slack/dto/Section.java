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
public class Section implements Block {
    String type = "section";
    Text text;

    public static Section from(String text){
        return Section.builder().text(Text.builder().text(text).type("mrkdwn").build()).build();
    }
}
