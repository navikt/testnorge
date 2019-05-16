package no.nav.registre.inst;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class InstSaveInHodejegerenRequest {
    private String id;
    private Kilde kilde;
}
