package no.nav.dolly.domain.resultset.entity.bestilling;

import static java.util.Objects.isNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsStatusRapport;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public class RsBestillingStatus {

    private Long id;
    private Integer antallIdenter;
    private Integer antallLevert;
    private boolean ferdig;
    private ZonedDateTime sistOppdatert;
    private String userId;
    private long gruppeId;
    private boolean stoppet;
    private String feil;
    private List<String> environments;
    private List<RsStatusRapport> status;

    private Long opprettetFraId;
    private RsBestilling bestilling;
    private String openamSent;
    private String opprettFraIdenter;

    private String malBestillingNavn;

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList();
        }
        return environments;
    }

    public List<RsStatusRapport> getStatus() {
        if (isNull(status)) {
            status = new ArrayList();
        }
        return status;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsBestilling {

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
