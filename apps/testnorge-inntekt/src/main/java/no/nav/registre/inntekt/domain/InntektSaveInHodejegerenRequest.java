package no.nav.registre.inntekt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class InntektSaveInHodejegerenRequest {
    private String kilde;
    private List<IdentMedData> identMedData;
}
