package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagsDTO {
    private Map<String, List<String>> personerTags;
    private String message;
    private String details;
    @JsonIgnore
    public static boolean hasOnlyTestnorgeTags(List<String> tags){
        return nonNull(tags) &&
                tags.contains("DOLLY") ||
                tags.contains("ARENASYNT");
    }

}
