package no.nav.testnav.libs.dto.arena.testnorge.aap.periode;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Periode {

    @JsonAlias({ "ENDRING_PERIODE", "endringPeriodeBegrunnelse" })
    private String endringPeriodeBegrunnelse;

    @JsonAlias({ "ENDRING_PERIODE_BEGRUNNELSE", "endringPeriodeTeller" })
    private Integer endringPeriodeTeller;

    @JsonAlias({ "ENDRING_UNNTAK_BEGRUNNELSE", "endringUnntakBegrunnelse" })
    private String endringUnntakBegrunnelse;

    @JsonAlias({ "ENDRING_UNNTAK", "endringUnntakTeller" })
    private Integer endringUnntakTeller;

    @JsonAlias({ "NULLSTILL", "nullstill" })
    private String nullstill;

    @JsonAlias({ "PERIODE_KODE", "periodeKode" })
    private String periodeKode;
}
