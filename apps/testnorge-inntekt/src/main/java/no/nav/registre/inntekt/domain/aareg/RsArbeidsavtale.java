package no.nav.registre.inntekt.domain.aareg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.inntekt.utils.JsonDateDeserializer;
import no.nav.registre.inntekt.utils.JsonDateSerializer;

import java.time.LocalDateTime;

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
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private LocalDateTime endringsdatoStillingsprosent;


    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private LocalDateTime endringsdatoLoenn;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private LocalDateTime sisteLoennsendringsdato;

    private Double stillingsprosent;
    private String yrke;
}
