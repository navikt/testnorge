package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IdentSearch {

    Integer page;
    Integer pageSize;
    Integer terminateAfter;
    String tag;
    List<String> tags;
    List<String> excludeTags;
    String ident;
    List<String> navn;

    public List<String> getNavn() {

        if (isNull(navn)) {
            navn = new ArrayList<>();
        }
        return navn;
    }
}