package no.nav.dolly.domain.resultset.entity.bestilling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

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
    private LocalDateTime sistOppdatert;
    private RsBrukerUtenFavoritter bruker;
    private long gruppeId;
    private boolean stoppet;
    private String feil;
    private List<String> environments;
    private List<RsStatusRapport> status;

    private Long opprettetFraId;
    private Long opprettetFraGruppeId;
    private RsBestilling bestilling;
    private String openamSent;
    private String opprettFraIdenter;

    private String malBestillingNavn;

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList<>();
        }
        return environments;
    }

    public List<RsStatusRapport> getStatus() {
        if (isNull(status)) {
            status = new ArrayList<>();
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

        private RsTpsfUtvidetBestilling tpsf;
        private Boolean navSyntetiskIdent;
        private List<String> importFraTps;
        private List<String> importFraPdl;
        private String kildeMiljoe;

        private RsPdldata pdlforvalter;
        private RsDigitalKontaktdata krrstub;
        private List<RsInstdata> instdata;
        private List<RsAareg> aareg;
        private List<OpprettSkattegrunnlag> sigrunstub;
        private InntektMultiplierWrapper inntektstub;
        private Arenadata arenaforvalter;
        private RsUdiPerson udistub;
        private PensjonData pensjonforvalter;
        private RsInntektsmelding inntektsmelding;
        private RsBregdata brregstub;
        private RsDokarkiv dokarkiv;
        private RsSykemelding sykemelding;
        private PdlPersondata pdldata;
        private RsTpsMessaging tpsMessaging;
    }
}
