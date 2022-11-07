package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

import lombok.*;
import no.nav.testnav.libs.dto.aareg.v1.*;

import java.util.Collections;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsAaregSyntetiseringsRequest {

    private RsSyntetiskArbeidsforhold arbeidsforhold;
    private String arkivreferanse;
    private List<String> environments;

    private Arbeidsavtale getArbeidsavtale(){
        var arbeidsforholdType = arbeidsforhold.getArbeidsforholdstype();
        Arbeidsavtale avtale = switch (arbeidsforholdType){
            case "ordinaertArbeidsforhold" -> OrdinaerArbeidsavtale.builder().build();
            case "maritimtArbeidsforhold" -> MaritimArbeidsavtale.builder().build();
            case "frilanserOppdragstakerHonorarPersonerMm'" -> FrilanserArbeidsavtale.builder().build();
            case "forenkletOppgjoersordning" -> ForenkletOppgjoersordningArbeidsavtale.builder().build();
        };

        avtale.setArbeidstidsordning(arbeidsforhold.getArbeidsavtale().getArbeidstidsordning());
        avtale.setYrke(arbeidsforhold.getArbeidsavtale().getYrke());
        avtale.setAntallTimerPrUke(arbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke());
        avtale.setStillingsprosent(arbeidsforhold.getArbeidsavtale().getStillingsprosent());
        avtale.setSistStillingsendring(arbeidsforhold.getArbeidsavtale().getEndringsdatoStillingsprosent());
        avtale.setSistLoennsendring(arbeidsforhold.getArbeidsavtale().getSisteLoennsendringsdato());

        return avtale;
    }

    public Arbeidsforhold mapToArbeidsforhold(){
        return Arbeidsforhold.builder()
                .arbeidsforholdId(arbeidsforhold.getArbeidsforholdID())
                .arbeidsgiver(Organisasjon.builder()
                        .organisasjonsnummer(((RsOrganisasjon) arbeidsforhold.getArbeidsgiver()).getOrgnummer())
                        .build())
                .arbeidstaker(Person.builder()
                        .offentligIdent(arbeidsforhold.getArbeidstaker().getIdent())
                        .build())
                .type(arbeidsforhold.getArbeidsforholdstype())
                .ansettelsesperiode(Ansettelsesperiode.builder()
                        .periode(Periode.builder()
                                .fom(arbeidsforhold.getAnsettelsesPeriode().getFom())
                                .fom(arbeidsforhold.getAnsettelsesPeriode().getTom())
                                .build())
                        .build())
                .arbeidsavtaler(Collections.singletonList(getArbeidsavtale()))
                .build();

    }
}
