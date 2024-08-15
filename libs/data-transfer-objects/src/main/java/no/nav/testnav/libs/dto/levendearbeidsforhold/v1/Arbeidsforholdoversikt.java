package no.nav.testnav.libs.dto.levendearbeidsforhold.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "navArbeidsforholdId",
        "arbeidstaker",
        "arbeidsgiver",
        "opplysningspliktig",
        "type",
        "ansattFom",
        "ansattTom",
        "yrke",
        "stillingsprosent",
        "permisjonPermitteringsprosent",
        "innrapportertEtterAOrdningen",
        "sistBekreftet",
        "varsler"
})
@Schema(description = "Informasjon om arbeidsforhold (overordnet)")
@SuppressWarnings("fb-contrib:CC_CYCLOMATIC_COMPLEXITY")
public class Arbeidsforholdoversikt {

    @Schema(description = "Arbeidsforhold-id i AAREG", example = "123456")
    private Long navArbeidsforholdId;

    private Person arbeidstaker;

    private OpplysningspliktigArbeidsgiver arbeidsgiver;

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

    @JsonIgnore
    public LocalDate getAnsattFom() {
        return ansattFom;
    }

    @JsonProperty("ansattFom")
    @Schema(description = "Fra-og-med-dato for ansettelsesperiode, format (ISO-8601): yyyy-MM-dd", example = "2014-07-01")
    public String getAnsattFomAsString() {
        return TimeUtil.toString(ansattFom);
    }

    @JsonIgnore
    public LocalDate getAnsattTom() {
        return ansattTom;
    }

    @JsonProperty("ansattTom")
    @Schema(description = "Til-og-med-dato for ansettelsesperiode, format (ISO-8601): yyyy-MM-dd", example = "2015-12-31")
    public String getAnsattTomAsString() {
        return TimeUtil.toString(ansattTom);
    }

    @JsonIgnore
    public LocalDateTime getSistBekreftet() {
        return sistBekreftet;
    }

    @JsonProperty("sistBekreftet")
    @Schema(description = "Tidspunkt for siste bekreftelse av arbeidsforhold, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2018-09-19T12:10:31")
    public String getSistBekreftetAsString() {
        return TimeUtil.toString(sistBekreftet);
    }
}
