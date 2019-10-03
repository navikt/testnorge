package no.nav.registre.arena.domain.aap.periode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Periode {
    private String endringPeriodeBegrunnelse;
    private Integer endringPeriodeTeller;
    private String endringUnntakBegrunnelse;
    private Integer endringUnntakTeller;
    private String nullstill;
    private String periodeKode;
}
