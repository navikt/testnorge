package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestilling {

    private List<String> environments;
    private List<RsOpprettSkattegrunnlag> sigrunstub;
    private RsDigitalKontaktdata krrstub;
    private List<RsArbeidsforhold> aareg;

    public List<RsArbeidsforhold> getAareg() {
        if (isNull(aareg)) {
            aareg = new ArrayList();
        }
        return aareg;
    }

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList();
        }
        return environments;
    }

    public List<RsOpprettSkattegrunnlag> getSigrunstub() {
        if (isNull(sigrunstub)) {
            sigrunstub = new ArrayList();
        }
        return sigrunstub;
    }
}
