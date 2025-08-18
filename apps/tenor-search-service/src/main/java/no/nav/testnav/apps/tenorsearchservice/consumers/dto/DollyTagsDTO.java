package no.nav.testnav.apps.tenorsearchservice.consumers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DollyTagsDTO {

    private Map<String, List<String>> personerTags;
    private String message;
    private String details;

    @JsonIgnore
    public static boolean hasDollyTag(List<String> tags) {

        return nonNull(tags) &&
                tags.contains("DOLLY");
    }
}
