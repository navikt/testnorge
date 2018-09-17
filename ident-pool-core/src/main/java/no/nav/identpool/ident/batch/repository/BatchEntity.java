package no.nav.identpool.ident.batch.repository;

import java.time.LocalDate;
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
@Table(name = "BATCH")
public class BatchEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batch_seq")
    @SequenceGenerator(name = "batch_seq", sequenceName = "BATCH_SEQ", allocationSize = 1)
    private Long identity;

    @NotNull
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    @NotNull
    @Column(name = "STARTDATO")
    private LocalDate startDato;

    @NotNull
    @Column(name = "SLUTTDATO")
    private LocalDate sluttDato;

    @NotNull
    @Column(name = "SISTOPPDATERT")
    private LocalDateTime sistOppdatert;

    @Column(name = "FEILMELDING")
    private String feilmelding;
}
