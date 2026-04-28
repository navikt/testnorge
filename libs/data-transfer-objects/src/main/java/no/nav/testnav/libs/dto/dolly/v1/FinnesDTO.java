package no.nav.testnav.libs.dto.dolly.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinnesDTO {

    @JsonProperty("iBruk")
    @Builder.Default
    private Map<String, Boolean> iBruk = Collections.emptyMap();
}