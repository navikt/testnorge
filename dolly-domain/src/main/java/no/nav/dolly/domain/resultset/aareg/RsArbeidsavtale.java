package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidsavtale {

    private String arbeidstidsordning;
    private String avloenningstype;
    private String yrke;
    private BigDecimal avtaltArbeidstimerPerUke;
    private BigDecimal stillingsprosent;
    private BigDecimal antallKonverterteTimer;
    private LocalDateTime endringsdatoStillingsprosent;
    private LocalDateTime sisteLoennsendringsdato;
}
