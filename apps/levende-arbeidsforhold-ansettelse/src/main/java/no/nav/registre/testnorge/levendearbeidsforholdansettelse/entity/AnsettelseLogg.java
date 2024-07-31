package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ansettelse_logg")
public class AnsettelseLogg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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