package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdreRequest {

    private List<List<Ordre>> sletting;
    private List<List<Ordre>> oppretting;
    private List<List<Ordre>> opplysninger1;
    private List<List<Ordre>> opplysninger2;
}
