package no.nav.dolly.domain.resultset.pdlforvalter;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsStatusIdent;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsPdlForvalterStatus {

    private List<RsStatusIdent> kontaktinfoDoedsbo;
    private List<RsStatusIdent> utenlandsid;
    private List<RsStatusIdent> falskIdentitet;
    private List<RsStatusIdent> pdlForvalter;

    public List<RsStatusIdent> getKontaktinfoDoedsbo() {
        if (isNull(kontaktinfoDoedsbo)) {
            kontaktinfoDoedsbo = new ArrayList<>();
        }
        return kontaktinfoDoedsbo;
    }

    public List<RsStatusIdent> getUtenlandsid() {
        if (isNull(utenlandsid)) {
            utenlandsid = new ArrayList<>();
        }
        return utenlandsid;
    }
}
