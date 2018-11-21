package no.nav.identpool.ajourhold.repository;

import java.time.LocalDateTime;
import javax.batch.runtime.BatchStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AJOURHOLD")
public class AjourholdEntity {
//TODO Samle sammen repository i en pakke
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
