package no.nav.testnav.apps.importfratpsfservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkdEndringsmeldingDTO {

    private List<SkdEndringsmelding> endringsmeldinger;

    public List<SkdEndringsmelding> getEndringsmeldinger() {
        return endringsmeldinger;
    }
}
