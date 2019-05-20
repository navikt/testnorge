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
import no.nav.dolly.domain.resultset.arenastub.RsArenadata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class BestKriterier {

    private List<RsArbeidsforhold> aareg;
    private RsDigitalKontaktdata krrStub;
    private List<RsOpprettSkattegrunnlag> sigrunStub;
    private RsArenadata arenaStub;
    private RsPdldata pdlforvalter;

    public List<RsArbeidsforhold> getAareg() {
        if(isNull(aareg)) {
            aareg = new ArrayList();
        }
        return aareg;
    }

    public List<RsOpprettSkattegrunnlag> getSigrunStub() {
        if (isNull(sigrunStub)) {
            sigrunStub = new ArrayList();
        }
        return sigrunStub;
    }
}
