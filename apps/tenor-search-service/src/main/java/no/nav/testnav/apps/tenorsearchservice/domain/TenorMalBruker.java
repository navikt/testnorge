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
@Table("tenor_mal_bruker")
public class TenorMalBruker {

    @Id
    private Long id;

    @Column("bruker_id")
    private String brukerId;

    @Column("brukernavn")
    private String brukernavn;

    @Column("brukertype")
    private TenorMalBrukerType brukertype;

    @Column("sist_oppdatert")
    private Instant sistOppdatert;
}
