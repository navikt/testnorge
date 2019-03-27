package no.nav.dolly.domain.jpa;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
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
}
