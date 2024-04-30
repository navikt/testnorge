package no.nav.testnav.apps.tenorsearchservice.consumers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DollyTagDTO {

    private String ident;
    private String[] tags;
    @JsonIgnore
    public boolean hasDollyTag() {
        return Objects.nonNull(tags) && Arrays.asList(tags).contains("DOLLY") ||
                Arrays.asList(tags).contains("ARENASYNT");
    }
}
