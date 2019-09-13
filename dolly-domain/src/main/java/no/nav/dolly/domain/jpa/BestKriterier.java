package no.nav.dolly.domain.jpa;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class BestKriterier {

    private List<RsArbeidsforhold> aareg;
	private RsDigitalKontaktdata krrstub;
    private RsUdiPerson udistub;
    private List<RsOpprettSkattegrunnlag> sigrunstub;
    private Arenadata arenaforvalter;
    private RsPdldata pdlforvalter;
    private List<RsInstdata> instdata;

    public List<RsArbeidsforhold> getAareg() {
        if(isNull(aareg)) {
            aareg = new ArrayList();
        }
        return aareg;
    }

    public List<RsOpprettSkattegrunnlag> getSigrunstub() {
        if (isNull(sigrunstub)) {
            sigrunstub = new ArrayList();
        }
        return sigrunstub;
    }
}
