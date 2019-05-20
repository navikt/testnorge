package no.nav.registre.sam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SamSaveInHodejegerenRequest {
    private String id;
    private Kilde kilde;
}
