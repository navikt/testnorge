package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsSyntetiskArbeidsavtale {

    private Integer antallKonverterteTimer;
    private String arbeidstidsordning;
    private String avloenningstype;
    private Double avtaltArbeidstimerPerUke;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate endringsdatoStillingsprosent;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate sisteLoennsendringsdato;
    private Double stillingsprosent;
    private String yrke;
}
