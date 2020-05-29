package no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Boadresse {

    private String adresse;
    private String land;
    private String kommune;
    private String postnr;
    private String bydel;
    private String offentligGateKode;
    private String offentligHusnr;
    private String offentligBokstav;
    private String offentligBolignr;
    private String matrikkelGardsnr;
    private String matrikkelBruksnr;
    private String matrikkelFestenr;
    private String matrikkelUndernr;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate fraDato;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate tilDato;
}
