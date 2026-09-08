package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("tenor_mal")
public class TenorMal {

    @Id
    private Long id;

    @Column("mal_navn")
    private String malNavn;

    @Column("mal_navn_normalisert")
    private String malNavnNormalisert;

    @Column("mal_type")
    private TenorMalType malType;

    @Column("soek_kriterier")
    private String soekKriterier;

    @Column("bruker_id")
    private Long brukerId;

    @Column("opprettet")
    private Instant opprettet;

    @Column("sist_oppdatert")
    private Instant sistOppdatert;
}
