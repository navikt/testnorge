package no.nav.testnav.libs.dto.yrkesskade.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class YrkesskadeRequest {

    @Schema(description = "Norsk fødselsnummer for den skadelidte")
    private String skadelidtIdentifikator;

    @Schema(description = "Rollen for den skadelidte. Rolletype blir validert av kodeverket rolletype")
    private RolleType rolletype;

    @Schema(description = "Norsk fødselsnummer for den personen som melder inn skaden")
    private String innmelderIdentifikator;

    @Schema(description = "Innmelders rolle. Det er arbeidsgiver som har meldeplikt for innmelding av yrkesskader. " +
            "I hovedssak skal virksomhetsrepresenant benyttes. Verdien blir sjekket mot kodeverket 'innmelderrolle'." +
            " Dersom det benyttes denSkadelidte, må innmelderIdentifikator være lik skadelidtIdentifikator")
    private InnmelderRolletype innmelderrolle;

    @Schema(description = """
            Inntil videre blir klassifisering brukt for å bestemme hvor skademeldingen skal rutes. Dersom skaden er en bagatellmessig skade,
            og bruker ikke har noen saker/journalposter på Tema YRK fra før, vil skademeldingen rutes til nytt saksbehandling system, Kompys, for videre prossessering.
            
                    Dersom skademeldingen blir rutet til Gosys, vil det ikke bli opprettet en sak i Infotrygd automatisk, som heller
                    ikke kan ferdigstilles.\s
                   \s
                    Kun saker rutet til Kompys vil vise saker automatisk.
                   \s
                    Standard er MANUELL
            """)
    private Klassifisering klassifisering;

    @Schema(description = "Organisasjonsnummer eller fødselsnummer innmelder sender på vegne av. Dersom det er en " +
            "virksomhetsrepresentant må paaVegneAv være det organisasjonsnummeret for virksomheten. " +
            "Dersom innmelder er denSkadelidte eller vergeEllerForesatt skal paaVegneAv være det være det samme som " +
            "skadelidtIdentifikator.")
    private String paaVegneAv;

    @Schema(description = "Tidstype bestemmer om det er en yrkesskade eller yrkessykdom. " +
            "Ett tidspunkt tilsier at det er en skade, mens periode er en sykdom")
    private Tidstype tidstype;

    @Schema(description = "Tidspunkt da skaden oppstod. Denne blir ignorert dersom tidstype er ulik 'tidspunkt'")
    private LocalDate skadetidspunkt;

    @Schema(description = "Perioder med sykdom. Disse blir ignorert dersom tidstype er ulik 'periode'")
    private List<Periode> perioder;

    @Schema(description = "Ekstern refaranse på skademelding - Kan benyttes for f.eks bestillingsnummer e.l")
    private String referanse;

    @Schema(description = "Ferdigstill sak dersom den er kommet til Kompys med resultat.")
    private FerdigstillSak ferdigstillSak;

    public List<Periode> getPerioder() {

        if (isNull(perioder)) {
            perioder = new ArrayList<>();
        }
        return perioder;
    }

    @Data
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Periode {

        @Schema(description = "Fra dato i perioden")
        private LocalDate fra;

        @Schema(description = "Til dato i perioden")
        private LocalDate til;
    }
}
