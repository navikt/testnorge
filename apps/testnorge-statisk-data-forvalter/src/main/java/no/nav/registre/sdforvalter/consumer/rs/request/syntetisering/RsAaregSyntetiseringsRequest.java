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
        Arbeidsavtale avtale;
        switch (arbeidsforholdType){
            case "maritimtArbeidsforhold" -> avtale = new MaritimArbeidsavtale();
            case "frilanserOppdragstakerHonorarPersonerMm'" -> avtale = new FrilanserArbeidsavtale();
            case "forenkletOppgjoersordning" ->  avtale = new ForenkletOppgjoersordningArbeidsavtale();
            default -> avtale = new OrdinaerArbeidsavtale();
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
        //todo map permisjon og utenlandsopphold
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
