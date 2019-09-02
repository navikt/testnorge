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
public class Tillegg {

    private String adresse1;
    private String adresse2;
    private String adresse3;
    private String postnr;
    private String kommunenr;
    private String gateKode;
    private String husnummer;
    private String husbokstav;
    private String bolignummer;
    private String bydel;
    private String postboksnr;
    private String postboksAnlegg;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate fraDato;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate tilDato;
}
