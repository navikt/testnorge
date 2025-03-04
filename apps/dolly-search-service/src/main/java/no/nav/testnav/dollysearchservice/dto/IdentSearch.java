package no.nav.testnav.dollysearchservice.dto;

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
    List<String> tags;
    String ident;
    List<String> navn;

    public List<String> getTags() {

        if (isNull(tags)) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public List<String> getNavn() {

        if (isNull(navn)) {
            navn = new ArrayList<>();
        }
        return navn;
    }
}