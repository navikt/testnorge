package no.nav.dolly.domain.resultset.entity.bestilling;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.inntektsstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import springfox.documentation.spring.web.json.Json;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsMalBestillingWrapper {

    private String malNavn;
    private RsMalBestilling mal;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsMalBestilling {

        private Integer antallIdenter;
        private String opprettFraIdenter;
        private List<String> environments;
        private Json tpsf;

        private RsPdldata pdlforvalter;
        private RsDigitalKontaktdata krrstub;
        private List<RsInstdata> instdata;
        private List<RsArbeidsforhold> aareg;
        private List<OpprettSkattegrunnlag> sigrunstub;
        private RsInntektsinformasjon inntektsstub;
        private Arenadata arenaforvalter;
        private RsUdiPerson udistub;
    }
}