package no.nav.testnav.libs.dto.dollysearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private Integer side;
    private Integer antall;
    private Integer seed;

    private List<String> miljoer;

    private PersonRequest personRequest;
}
