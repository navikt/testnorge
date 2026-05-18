package no.nav.testnav.altinn3tilgangservice.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organisasjon_tilgang")
public class OrganisasjonTilgang {

    @Id
    @Column("id")
    private Long id;

    @Column("organisajon_nummer")
    private String organisasjonNummer;

    @Column("miljoe")
    private String miljoe;

    @Transient
    private String feilmelding;
}