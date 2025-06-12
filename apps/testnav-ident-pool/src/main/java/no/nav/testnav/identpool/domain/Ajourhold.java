package no.nav.testnav.identpool.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AJOURHOLD")
public class Ajourhold {

    @Id
    @Column("ID")
    private Long identity;

    @NotNull
    @Column("STATUS")
    private BatchStatus status;

    @Column("MELDING")
    private String melding;

    @NotNull
    @Column("SISTOPPDATERT")
    private LocalDateTime sistOppdatert;

    @Column("FEILMELDING")
    private String feilmelding;
}
