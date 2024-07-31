package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "ansettelse_logg")
public class AnsettelseLogg {
    @Id
    @ColumnDefault("nextval('ansettelse_logg_id_seq'::regclass)")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "organisasjonsnummer", nullable = false)
    private String organisasjonsnummer;

    @Size(max = 255)
    @NotNull
    @Column(name = "folkeregisterident", nullable = false)
    private String folkeregisterident;

    @NotNull
    @Column(name = "nav_arbeidsforhold_id", nullable = false)
    private Long navArbeidsforholdId;

    @NotNull
    @Column(name = "\"timestamp\"", nullable = false)
    private OffsetDateTime timestamp;

    @NotNull
    @Column(name = "ansattfra", nullable = false)
    private LocalDate ansattfra;

    @Size(max = 255)
    @Column(name = "arbeidsforhold_type")
    private String arbeidsforholdType;

    @Column(name = "stillingsprosent")
    private BigDecimal stillingsprosent;

}