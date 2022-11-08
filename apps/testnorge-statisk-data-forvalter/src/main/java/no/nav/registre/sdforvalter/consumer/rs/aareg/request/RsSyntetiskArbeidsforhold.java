package no.nav.registre.sdforvalter.consumer.rs.aareg.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.Ansettelsesperiode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsSyntetiskArbeidsforhold {

    private RsSyntetiskPeriode ansettelsesPeriode;
    private List<RsSyntetiskAntallTimerForTimeloennet> antallTimerForTimeloennet;
    private RsSyntetiskArbeidsavtale arbeidsavtale;
    private String arbeidsforholdID;
    private Long arbeidsforholdIDnav;
    private String arbeidsforholdstype;
    private RsAktoer arbeidsgiver;
    private RsSyntPerson arbeidstaker;
    private List<RsSyntetiskPermisjon> permisjon;
    private List<RsSyntetiskUtenlandsopphold> utenlandsopphold;

    public List<RsSyntetiskPermisjon> getPermisjon() {
        if (permisjon == null) {
            permisjon = new ArrayList<>();
        }
        return permisjon;
    }

    public List<RsSyntetiskUtenlandsopphold> getUtenlandsopphold() {
        if (utenlandsopphold == null) {
            utenlandsopphold = new ArrayList<>();
        }
        return utenlandsopphold;
    }

    public List<RsSyntetiskAntallTimerForTimeloennet> getAntallTimerForTimeloennet() {
        if (antallTimerForTimeloennet == null) {
            antallTimerForTimeloennet = new ArrayList<>();
        }
        return antallTimerForTimeloennet;
    }

    @JsonIgnore
    public Arbeidsforhold toArbeidsforhold() {
        return Arbeidsforhold.builder()
                .arbeidsforholdId(arbeidsforholdID)
                .antallTimerForTimeloennet(getAntallTimerForTimeloennet().stream()
                        .map(RsSyntetiskAntallTimerForTimeloennet::toAntallTimerForTimeloennet).toList())
                .arbeidsgiver(isNull(arbeidsgiver) ? null : ((RsOrganisasjon) arbeidsgiver).toOrganisasjon())
                .arbeidstaker(isNull(arbeidstaker) ? null : arbeidstaker.toPerson())
                .type(arbeidsforholdstype)
                .ansettelsesperiode(isNull(ansettelsesPeriode) ? null : Ansettelsesperiode.builder()
                        .periode(ansettelsesPeriode.toPeriode()).build())
                .arbeidsavtaler(isNull(arbeidsavtale) ? Collections.emptyList() : Collections.singletonList(arbeidsavtale.toArbeidsavtale(arbeidsforholdstype)))
                .permisjonPermitteringer(getPermisjon().stream().map(RsSyntetiskPermisjon::toPermisjonPermittering).toList())
                .utenlandsopphold(getUtenlandsopphold().stream().map(RsSyntetiskUtenlandsopphold::toUtenlandsopphold).toList())
                .build();

    }
}
