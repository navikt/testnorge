package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Informasjon om arbeidsforhold")
@SuppressWarnings({"pmd:TooManyFields", "fb-contrib:CC_CYCLOMATIC_COMPLEXITY"})
public class Arbeidsforhold {

    @Schema(description = "Arbeidsforhold-id i AAREG", example = "123456")
    private Long navArbeidsforholdId;

    @Schema(description = "Arbeidsforhold-id fra opplysningspliktig", example = "abc-321")
    private String arbeidsforholdId;

    private LocalDateTime registrert;

    @Schema(description = "Arbeidstaker")
    private Person arbeidstaker;

    @Schema(description = "Arbeidsgiver - organisasjon eller person")
    private OpplysningspliktigArbeidsgiver arbeidsgiver;

    @Schema(description = "Opplysningspliktig - organisasjon eller person")
    private OpplysningspliktigArbeidsgiver opplysningspliktig;

    @Schema(description = "Arbeidsforholdtype (kodeverk: Arbeidsforholdtyper)", example = "ordinaertArbeidsforhold")
    private String type;

    @Schema(description = "Ansettelsesperiode")
    private Ansettelsesperiode ansettelsesperiode;

    @Schema(description = "Liste av arbeidsavtaler - gjeldende og evt. med historikk")
    private List<Arbeidsavtale> arbeidsavtaler;

    @Schema(description = "Liste av permisjoner og/eller permitteringer")
    private List<PermisjonPermittering> permisjonPermitteringer;

    @Schema(description = "Liste av antall timer med timel&oslash;nn")
    private List<AntallTimerForTimeloennet> antallTimerForTimeloennet;

    @Schema(description = "Liste av utenlandsopphold")
    private List<Utenlandsopphold> utenlandsopphold;

    @Schema(description = "Liste av unike varsler for ulike entiter")
    private List<Varsel> varsler;

    @Schema(description = "Er arbeidsforholdet innrapportert via a-ordningen?")
    private Boolean innrapportertEtterAOrdningen;

    private LocalDateTime sistBekreftet;
}
