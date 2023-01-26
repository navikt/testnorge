package no.nav.testnav.apps.organisasjontilgangservice.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORGANISASJON_TILGANG")
public class OrganisasjonTilgang {

    @Id
    @Column("ID")
    private Long id;

    @Column("ORGANISAJON_NUMMER")
    private String organisasjonNummer;

    @Column("miljoe")
    private String miljoe;
}