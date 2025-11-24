package no.nav.testnav.libs.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vilkaar implements Serializable {

    @JsonAlias({ "KODE", "kode" })
    private String kode;

    @JsonAlias({ "STATUS", "status" })
    private String status;
}
