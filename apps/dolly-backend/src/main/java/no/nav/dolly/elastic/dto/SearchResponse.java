package no.nav.dolly.elastic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.elastic.ElasticBestilling;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {

    private Long totalHits;
    private Float score;
    private String took;
    private List<String> identer;
    private List<ElasticBestilling> registre;
    private String error;

    public List<String> getIdenter() {

        if (isNull(identer)) {
            identer = new ArrayList<>();
        }
        return identer;
    }

    public List<ElasticBestilling> getRegistre() {

        if (isNull(registre)) {
            registre = new ArrayList<>();
        }
        return registre;
    }
}