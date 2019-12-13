package no.nav.registre.arena.domain.aap.vilkaar;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vilkaar {

    @JsonAlias({ "KODE", "kode" })
    private String kode;

    @JsonAlias({ "STATUS", "status" })
    private String status;
}
