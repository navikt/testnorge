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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "navArbeidsforholdId",
        "arbeidsforholdId",
        "arbeidstaker",
        "arbeidsgiver",
        "opplysningspliktig",
        "type",
        "ansettelsesperiode",
        "arbeidsavtaler",
        "permisjonPermitteringer",
        "antallTimerForTimeloennet",
        "utenlandsopphold",
        "varsler",
        "innrapportertEtterAOrdningen",
        "registrert",
        "sistBekreftet",
        "sporingsinformasjon"
})
@Schema(description = "Informasjon om arbeidsforhold")
@SuppressWarnings({"pmd:TooManyFields", "fb-contrib:CC_CYCLOMATIC_COMPLEXITY"})
public class Arbeidsforhold {

    @Schema(description = "Arbeidsforhold-id i AAREG", example = "123456")
    private Long navArbeidsforholdId;

    @Schema(description = "Arbeidsforhold-id fra opplysningspliktig", example = "abc-321")
    private String arbeidsforholdId;

    private LocalDateTime registrert;

    private Person arbeidstaker;

    private OpplysningspliktigArbeidsgiver arbeidsgiver;

    private OpplysningspliktigArbeidsgiver opplysningspliktig;

    @Schema(description = "Arbeidsforholdtype (kodeverk: Arbeidsforholdtyper)", example = "ordinaertArbeidsforhold")
    private String type;

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

    private Sporingsinformasjon sporingsinformasjon;

    @JsonIgnore
    public LocalDateTime getRegistrert() {
        return registrert;
    }

    @JsonProperty("registrert")
    @Schema(description = "Tidspunkt for registrering av arbeidsforhold, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2018-09-18T11:12:29")
    public String getRegistrertAsString() {
        return TimeUtil.toString(registrert);
    }

    @JsonProperty("registrert")
    public void setRegistrertAsString(String registrert) {
        this.registrert = TimeUtil.toLocalDateTime(registrert);
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

    @JsonProperty("sistBekreftet")
    public void setSistBekreftetAsString(String sistBekreftet) {
        this.sistBekreftet = TimeUtil.toLocalDateTime(sistBekreftet);
    }

    @Override
    public String toString() {
        return ("Arbeidsforhold: [" + navArbeidsforholdId + ", " + arbeidsforholdId + ", " + ansettelsesperiode + ", " + arbeidsavtaler);
    }

    public List<Arbeidsavtale> getArbeidsavtaler() {

        if (isNull(arbeidsavtaler)) {
            arbeidsavtaler = new ArrayList<>();
        }
        return arbeidsavtaler;
    }

    public List<PermisjonPermittering> getPermisjonPermitteringer() {

        if (isNull(permisjonPermitteringer)) {
            permisjonPermitteringer = new ArrayList<>();
        }
        return permisjonPermitteringer;
    }

    public List<AntallTimerForTimeloennet> getAntallTimerForTimeloennet() {

        if (isNull(antallTimerForTimeloennet)) {
            antallTimerForTimeloennet = new ArrayList<>();
        }
        return antallTimerForTimeloennet;
    }

    public List<Utenlandsopphold> getUtenlandsopphold() {

        if (isNull(utenlandsopphold)) {
            utenlandsopphold = new ArrayList<>();
        }
        return utenlandsopphold;
    }

    public List<Varsel> getVarsler() {

        if (isNull(varsler)) {
            varsler = new ArrayList<>();
        }
        return varsler;
    }
}
