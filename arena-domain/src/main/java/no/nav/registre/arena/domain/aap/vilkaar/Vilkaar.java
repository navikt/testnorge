package no.nav.registre.arena.domain.aap.vilkaar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class Vilkaar {
    private String kode;
    private String status;
}
