package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Informasjon om arbeidsforhold (overordnet)")
@SuppressWarnings("fb-contrib:CC_CYCLOMATIC_COMPLEXITY")
public class Arbeidsforholdoversikt {

    @Schema(description = "Arbeidsforhold-id i AAREG", example = "123456")
    private Long navArbeidsforholdId;

    @Schema(description = "Arbeidstaker")
    private Person arbeidstaker;

    @Schema(description = "Arbeidsgiver - organisasjon eller person")
    private OpplysningspliktigArbeidsgiver arbeidsgiver;

    @Schema(description = "Opplysningspliktig - organisasjon eller person")
    private OpplysningspliktigArbeidsgiver opplysningspliktig;

    @Schema(description = "Arbeidsforholdtype (kodeverk: Arbeidsforholdtyper)", example = "ordinaertArbeidsforhold")
    private String type;

    private LocalDate ansattFom;

    private LocalDate ansattTom;

    @Schema(description = "Yrke (kodeverk: Yrker)", example = "2130123")
    private String yrke;

    @Schema(description = "Stillingsprosent", example = "49.5")
    private Double stillingsprosent;

    @Schema(description = "Prosent for permisjon eller permittering (aggregert)", example = "50.5")
    private Double permisjonPermitteringsprosent;

    @Schema(description = "Er arbeidsforholdet innrapportert via a-ordningen?")
    private Boolean innrapportertEtterAOrdningen;

    private LocalDateTime sistBekreftet;

    @Schema(description = "Liste av unike varsler for ulike entiter")
    private List<Varsel> varsler;
}
