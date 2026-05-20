package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdreRequest {

    private List<Ordre> sletting;
    private List<Ordre> oppretting;
    private List<Ordre> merge;
    private List<Ordre> opplysninger;
}
