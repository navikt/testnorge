package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagsDTO {

    private Map<String, List<String>> personerTags;
    private String message;
    private String details;
}
