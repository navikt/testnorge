package no.nav.testnav.apps.templatesearchservice.domain;

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
@Table("tenor_person_mal")
public class TenorPersonMal {

    @Id
    @Column("id")
    private Long id;

    @Column("mal_navn")
    private String malNavn;

    @Column("soek_kriterier")
    private String soekKriterier;

    @Column("bruker_id")
    private String brukerId;

    @Column("brukernavn")
    private String brukernavn;

    @Column("brukertype")
    private TenorMalBrukerType brukertype;

    @Column("opprettet")
    private Instant opprettet;

    @Column("sist_oppdatert")
    private Instant sistOppdatert;
}
