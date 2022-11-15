package no.nav.registre.sdforvalter.consumer.rs.aareg.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import no.nav.testnav.libs.dto.aareg.v1.Arbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.MaritimArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.FrilanserArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.ForenkletOppgjoersordningArbeidsavtale;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsSyntetiskArbeidsavtale {

    private Integer antallKonverterteTimer;
    private String arbeidstidsordning;
    private String avloenningstype;
    private Double avtaltArbeidstimerPerUke;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate endringsdatoStillingsprosent;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate sisteLoennsendringsdato;
    private Double stillingsprosent;
    private String yrke;

    @JsonIgnore
    public Arbeidsavtale toArbeidsavtale(String arbeidsforholdstype) {
        Arbeidsavtale avtale;
        switch (arbeidsforholdstype) {
            case "maritimtArbeidsforhold" -> avtale = new MaritimArbeidsavtale();
            case "frilanserOppdragstakerHonorarPersonerMm'" -> avtale = new FrilanserArbeidsavtale();
            case "forenkletOppgjoersordning" -> avtale = new ForenkletOppgjoersordningArbeidsavtale();
            default -> avtale = new OrdinaerArbeidsavtale();
        }

        avtale.setArbeidstidsordning(arbeidstidsordning);
        avtale.setYrke(yrke);
        avtale.setAntallTimerPrUke(avtaltArbeidstimerPerUke);
        avtale.setStillingsprosent(stillingsprosent);
        avtale.setSistStillingsendring(endringsdatoStillingsprosent);
        avtale.setSistLoennsendring(sisteLoennsendringsdato);
        return avtale;
    }
}
