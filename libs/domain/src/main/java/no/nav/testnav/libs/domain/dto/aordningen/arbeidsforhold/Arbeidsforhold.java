package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "navArbeidsforholdId", "arbeidsforholdId", "arbeidstaker", "arbeidsgiver", "opplysningspliktig", "type", "ansettelsesperiode", "arbeidsavtaler", "permisjonPermitteringer",
        "antallTimerForTimeloennet", "utenlandsopphold", "innrapportertEtterAOrdningen", "registrert", "sistBekreftet", "sporingsinformasjon" })
@ApiModel(
        description = "Informasjon om arbeidsforhold"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Arbeidsforhold {

    @ApiModelProperty(
            notes = "Arbeidsforhold-id i AAREG",
            example = "123456"
    )
    private Long navArbeidsforholdId;
    @ApiModelProperty(
            notes = "Arbeidsforhold-id fra opplysningspliktig",
            example = "abc-321"
    )
    private String arbeidsforholdId;
    private LocalDateTime registrert;
    @ApiModelProperty(
            notes = "Arbeidstaker"
    )
    private Person arbeidstaker;
    @ApiModelProperty(
            notes = "Arbeidsgiver - organisasjon eller person"
    )
    private OpplysningspliktigArbeidsgiver arbeidsgiver;
    @ApiModelProperty(
            notes = "Opplysningspliktig - organisasjon eller person"
    )
    private OpplysningspliktigArbeidsgiver opplysningspliktig;
    @ApiModelProperty(
            notes = "Arbeidsforholdtype (kodeverk: Arbeidsforholdtyper)",
            example = "ordinaertArbeidsforhold"
    )
    private String type;
    @ApiModelProperty(
            notes = "Ansettelsesperiode"
    )
    private Ansettelsesperiode ansettelsesperiode;
    @ApiModelProperty(
            notes = "Liste av arbeidsavtaler - gjeldende og evt. med historikk"
    )
    private List<Arbeidsavtale> arbeidsavtaler;
    @ApiModelProperty(
            notes = "Liste av permisjoner og/eller permitteringer"
    )
    private List<PermisjonPermittering> permisjonPermitteringer;
    @ApiModelProperty(
            notes = "Liste av antall timer med timel&oslash;nn"
    )
    private List<AntallTimerForTimeloennet> antallTimerForTimeloennet;
    @ApiModelProperty(
            notes = "Liste av utenlandsopphold"
    )
    private List<Utenlandsopphold> utenlandsopphold;
    @ApiModelProperty(
            notes = "Er arbeidsforholdet innrapportert via A-Ordningen?"
    )
    private Boolean innrapportertEtterAOrdningen;
    private LocalDateTime sistBekreftet;
    @ApiModelProperty(
            notes = "Informasjon om opprettelse og endring av objekt"
    )
    private Sporingsinformasjon sporingsinformasjon;
}