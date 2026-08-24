package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAareg {

    @Schema(description = "Gyldige verdier finnes i kodeverk 'Arbeidsforholdstyper'")
    private String arbeidsforholdstype;

    private String arbeidsforholdId;

    private RsAnsettelsesPeriode ansettelsesPeriode;

    private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;

    private RsArbeidsavtale arbeidsavtale;

    private List<RsPermittering> permittering;

    private List<RsPermisjon> permisjon;

    private List<RsFartoy> fartoy;

    private List<RsUtenlandsopphold> utenlandsopphold;

    private RsAktoer arbeidsgiver;

    @JsonIgnore
    private Map<String, Identifikasjon> identifikasjon;

    @Schema(description = "Angir periode oppdateringen gjelder fra", type = "string", pattern="^\\d{4}-\\d{2}$")
    private YearMonth navArbeidsforholdPeriode;

    @Schema(description = "Angir om posten er oppdatering")
    private Boolean isOppdatering;

    @JsonIgnore
    private Integer tempId;

    public List<RsAntallTimerIPerioden> getAntallTimerForTimeloennet() {
        if (isNull(antallTimerForTimeloennet)) {
            antallTimerForTimeloennet = new ArrayList<>();
        }
        return antallTimerForTimeloennet;
    }

    public List<RsPermittering> getPermittering() {
        if (isNull(permittering)) {
            permittering = new ArrayList<>();
        }
        return permittering;
    }

    public List<RsPermisjon> getPermisjon() {
        if (isNull(permisjon)) {
            permisjon = new ArrayList<>();
        }
        return permisjon;
    }

    public List<RsFartoy> getFartoy() {
        if (isNull(fartoy)) {
            fartoy = new ArrayList<>();
        }
        return fartoy;
    }

    public List<RsUtenlandsopphold> getUtenlandsopphold() {
        if (isNull(utenlandsopphold)) {
            utenlandsopphold = new ArrayList<>();
        }
        return utenlandsopphold;
    }

    public Map<String, Identifikasjon> getIdentifikasjon() {
        if (isNull(identifikasjon)) {
            identifikasjon = new HashMap<>();
        }
        return identifikasjon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identifikasjon {

        private String arbeidsforholdId;
        private Long navArbeidsforholdId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RsAareg rsAareg = (RsAareg) o;

        return new EqualsBuilder()
                .append(getArbeidsforholdstype(), rsAareg.getArbeidsforholdstype())
                .append(getArbeidsforholdId(), rsAareg.getArbeidsforholdId())
                .append(getAnsettelsesPeriode(), rsAareg.getAnsettelsesPeriode())
                .append(getAntallTimerForTimeloennet(), rsAareg.getAntallTimerForTimeloennet())
                .append(getArbeidsavtale(), rsAareg.getArbeidsavtale())
                .append(getPermittering(), rsAareg.getPermittering())
                .append(getPermisjon(), rsAareg.getPermisjon())
                .append(getFartoy(), rsAareg.getFartoy())
                .append(getUtenlandsopphold(), rsAareg.getUtenlandsopphold())
                .append(getArbeidsgiver(), rsAareg.getArbeidsgiver())
                .append(getIdentifikasjon(), rsAareg.getIdentifikasjon())
                .append(getNavArbeidsforholdPeriode(), rsAareg.getNavArbeidsforholdPeriode())
                .append(getIsOppdatering(), rsAareg.getIsOppdatering()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getArbeidsforholdstype())
                .append(getArbeidsforholdId())
                .append(getAnsettelsesPeriode())
                .append(getAntallTimerForTimeloennet())
                .append(getArbeidsavtale())
                .append(getPermittering())
                .append(getPermisjon())
                .append(getFartoy())
                .append(getUtenlandsopphold())
                .append(getArbeidsgiver())
                .append(getIdentifikasjon())
                .append(getNavArbeidsforholdPeriode())
                .append(getIsOppdatering())
                .append(getTempId())
                .toHashCode();
    }
}