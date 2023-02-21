package no.nav.testnav.identpool.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AJOURHOLD")
public class Ajourhold {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ajourhold_seq")
    @SequenceGenerator(name = "ajourhold_seq", sequenceName = "AJOURHOLD_SEQ", allocationSize = 1)
    private Long identity;

    @NotNull
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    @NotNull
    @Column(name = "SISTOPPDATERT")
    private LocalDateTime sistOppdatert;

    @Column(name = "FEILMELDING")
    private String feilmelding;
}
