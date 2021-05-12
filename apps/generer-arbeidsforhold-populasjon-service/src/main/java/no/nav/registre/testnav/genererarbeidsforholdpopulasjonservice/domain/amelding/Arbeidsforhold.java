package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Id;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.PermisjonCount;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Value
@Slf4j
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Arbeidsforhold extends Generated implements Id {
    String id;
    String type;
    LocalDate startdato;
    LocalDate sluttdato;
    Float antallTimerPerUke;
    String yrke;
    String arbeidstidsordning;
    Float stillingsprosent;
    LocalDate sisteLoennsendringsdato;
    List<Permisjon> permisjoner;
    String historikk;
    List<Avvik> avvik;
    Fartoey fartoey;
    List<Inntekt> inntekter;
    String virksomhetsnummer;
    String opplysningspliktig;

    public Arbeidsforhold(ArbeidsforholdDTO dto, String virksomhetsnummer, String opplysningspliktig) {
        this.virksomhetsnummer = virksomhetsnummer;
        this.opplysningspliktig = opplysningspliktig;
        id = dto.getArbeidsforholdId();
        type = dto.getTypeArbeidsforhold();
        startdato = dto.getStartdato();
        sluttdato = dto.getSluttdato();
        antallTimerPerUke = dto.getAntallTimerPerUke();
        yrke = dto.getYrke();
        arbeidstidsordning = dto.getArbeidstidsordning();
        stillingsprosent = dto.getStillingsprosent();
        sisteLoennsendringsdato = dto.getSisteLoennsendringsdato();
        permisjoner = dto.getPermisjoner().stream().map(Permisjon::new).collect(Collectors.toList());
        historikk = dto.getHistorikk();
        avvik = dto.getAvvik().stream().map(Avvik::new).collect(Collectors.toList());
        fartoey = dto.getFartoey() == null ? null : new Fartoey(dto.getFartoey());
        inntekter = dto.getInntekter().stream().map(Inntekt::new).collect(Collectors.toList());
    }

    public Arbeidsforhold(ArbeidsforholdResponse response, String id, String virksomhetsnummer, String opplysningspliktig) {
        this.id = id;
        this.virksomhetsnummer = virksomhetsnummer;
        this.opplysningspliktig = opplysningspliktig;
        type = response.getArbeidsforholdType();
        startdato = response.getStartdato();
        sluttdato = format(response.getSluttdato());
        antallTimerPerUke = response.getAntallTimerPerUkeSomEnFullStillingTilsvarer();
        yrke = response.getYrke();
        arbeidstidsordning = response.getArbeidstidsordning();
        stillingsprosent = response.getStillingsprosent();
        sisteLoennsendringsdato = response.getSisteLoennsendringsdato();
        permisjoner = response.getPermisjoner() == null
                ? new ArrayList<>()
                : response.getPermisjoner().stream().map(Permisjon::new).collect(Collectors.toList());
        historikk = response.getHistorikk();
        avvik = response.getAvvik() == null
                ? new ArrayList<>()
                : Collections.singletonList(new Avvik(response.getAvvik()));
        fartoey = response.getFartoey() == null ? null : new Fartoey(response.getFartoey());
        inntekter = response.getInntekter() == null
                ? new ArrayList<>()
                : response.getInntekter().stream().map(Inntekt::new).collect(Collectors.toList());
    }


    public ArbeidsforholdRequest toSynt(Integer endringer, LocalDate kaldermnd) {
        var permisjonCount = new PermisjonCount(permisjoner);
        return ArbeidsforholdRequest
                .builder()
                .antallTimerPerUkeSomEnFullStillingTilsvarer(nullToEmpty(antallTimerPerUke))
                .arbeidsforholdType(nullToEmpty(type))
                .arbeidstidsordning(nullToEmpty(arbeidstidsordning))
                .permisjon(permisjonCount.getPermisjon())
                .permisjonMedForeldrepenger(permisjonCount.getPermisjonMedForeldrepenger())
                .permisjonVedMilitaertjeneste(permisjonCount.getPermisjonVedMilitaertjeneste())
                .permittering(permisjonCount.getPermittering())
                .velferdspermisjon(permisjonCount.getVelferdspermisjon())
                .utdanningspermisjon(permisjonCount.getUtdanningspermisjon())
                .rapporteringsmaaned(formatKaldenermaand(kaldermnd))
                .sisteDatoForStillingsprosentendring(kaldermnd)
                .sisteLoennsendringsdato(sisteLoennsendringsdato)
                .sluttdato(format(sluttdato))
                .startdato(startdato)
                .stillingsprosent(nullToEmpty(stillingsprosent))
                .yrke(nullToEmpty(yrke))
                .historikk(historikk)
                .numEndringer(endringer)
                .fartoey(fartoey != null ? fartoey.toSynt() : null)
                .permisjoner(permisjoner == null ? null : permisjoner.stream().map(Permisjon::toSynt).collect(Collectors.toList()))
                .inntekter(inntekter == null ? null : inntekter.stream().map(Inntekt::toSynt).collect(Collectors.toList()))
                .antallInntekter(inntekter == null ? 0 : inntekter.size())
                .avvik(toSyntAvvik(avvik))
                .build();
    }

    public boolean isForenklet() {
        return type.equals("forenkletOppgjoersordning");
    }

}
