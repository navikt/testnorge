package no.nav.registre.inst;

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
public class InstSaveInHodejegerenRequest {
    private String id;
    private Kilde kilde;
}
