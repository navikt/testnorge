package no.nav.dolly.domain.resultset.entity.bestilling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
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
    private Set<String> environments;
    private List<RsStatusRapport> status;

    private Long opprettetFraId;
    private Long opprettetFraGruppeId;
    private Long gjenopprettetFraIdent;
    private RsBestilling bestilling;
    private String opprettFraIdenter;

    private String malBestillingNavn;

    public Set<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new HashSet<>();
        }
        return environments;
    }

    public List<RsStatusRapport> getStatus() {
        if (isNull(status)) {
            status = new ArrayList<>();
        }
        return status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsBestilling {

        private Boolean navSyntetiskIdent;
        private List<String> importFraPdl;
        private String kildeMiljoe;
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
        private RsHistark histark;
        private RsSykemelding sykemelding;
        private PdlPersondata pdldata;
        private RsTpsMessaging tpsMessaging;
        private BankkontoData bankkonto;
        private RsSkjerming skjerming;
        private ArbeidsplassenCVDTO arbeidsplassenCV;
    }
}
