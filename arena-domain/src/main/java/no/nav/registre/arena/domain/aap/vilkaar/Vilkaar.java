package no.nav.registre.arena.domain.aap.vilkaar;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
public class Vilkaar {
    private String kode;
    private String status;
}
