package no.nav.registre.aareg.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import no.nav.registre.aareg.util.JsonDateSerializer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsArbeidsavtale {

    private Integer antallKonverterteTimer;
    private String arbeidstidsordning;
    private String avloenningstype;
    private Double avtaltArbeidstimerPerUke;

    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDateTime endringsdatoStillingsprosent;

    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDateTime sisteLoennsendringsdato;

    private Double stillingsprosent;
    private String yrke;
}
