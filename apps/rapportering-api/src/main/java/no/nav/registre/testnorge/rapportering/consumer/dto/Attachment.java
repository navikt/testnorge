package no.nav.registre.testnorge.rapportering.consumer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class Attachment {
    String fallback;
    List<Action> actions;

    public Attachment(String fallback, Action... actions) {
        this.fallback = fallback;
        this.actions = Arrays.asList(actions);
    }
}
