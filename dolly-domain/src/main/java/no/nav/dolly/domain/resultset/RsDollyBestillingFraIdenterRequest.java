package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBasisBestilling;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsDollyBestillingFraIdenterRequest extends RsDollyBestilling {

    List<String> opprettFraIdenter;
    private RsTpsfBasisBestilling tpsf;

    public List<String> getOpprettFraIdenter() {
        if (isNull(opprettFraIdenter)) {
            opprettFraIdenter = new ArrayList<>();
        }
        return opprettFraIdenter;
    }
}