package no.nav.dolly.domain.resultset.etterlatte;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtterlatteYtelse {

    @Schema(description = "Type ytelse det søkes vedtak for, det må finnes en død og en gjenlevende i forholdet " +
            "(som er kandidat for omstillingssøknad), og ved barnepensjon, minst et barn")
    private YtelseType ytelse;

    @Schema(description = "Ident for person det søkes vedtak for (feltet er ikke påkrevd), " +
            "det er egentlig kun ved flere barn at dette feltet har noen nytte",
            example = "f.eks. en av barna i liste (tomt felt indikerer alle barn). " +
                    "Merk at feltet kan ikke ha verdi som indikerer avdødd, det gir feil ved opprettingen")
    private String soeker;

    public enum YtelseType {
        BARNEPENSJON,
        OMSTILLINGSSTOENAD
    }
}
