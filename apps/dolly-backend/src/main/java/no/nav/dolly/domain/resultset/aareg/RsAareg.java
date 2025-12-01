package no.nav.dolly.domain.resultset.aareg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
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

    @EqualsAndHashCode.Exclude
    private Map<String, Identifikasjon> identifikasjon;

    @Schema(description = "Angir periode oppdateringen gjelder fra", type = "string", pattern="^\\d{4}-\\d{2}$")
    private YearMonth navArbeidsforholdPeriode;

    @Schema(description = "Angir om posten er oppdatering")
    private Boolean isOppdatering;

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
}