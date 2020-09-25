package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsArbeidsavtale {

    @Schema(description = "Gyldige verdier finnes i kodeverk 'Arbeidstidsordninger'",
            type = "String",
            required = true)
    private String arbeidstidsordning;

    @Schema(description = "Gyldige verdier finnes i kodeverk 'Yrker'",
            type = "String",
            required = true)
    private String yrke;

    private BigDecimal avtaltArbeidstimerPerUke;

    private BigDecimal stillingsprosent;

    private BigDecimal antallKonverterteTimer;

    @Schema(type = "LocalDateTime")
    private LocalDateTime endringsdatoStillingsprosent;
}
