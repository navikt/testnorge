package no.nav.dolly.domain.resultset.entity.bestilling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsMalBestillingWrapper {

    private Map<String, Set<RsMalBestilling>> malbestillinger;

    public Map<String, Set<RsMalBestilling>> getMalbestillinger() {

        if (isNull(malbestillinger)) {
            malbestillinger = new TreeMap<>();
        }
        return malbestillinger;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsMalBestilling {

        private Long id;
        private String malNavn;
        private RsBestilling bestilling;
        private RsBrukerUtenFavoritter bruker;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsBestilling {

        private Integer antallIdenter;
        private Boolean navSyntetiskIdent;
        private String opprettFraIdenter;
        private List<String> environments;
        private RsTpsfUtvidetBestilling tpsf;

        private RsPdldata pdlforvalter;
        private PdlPersondata pdldata;
        private RsDigitalKontaktdata krrstub;
        private List<RsInstdata> instdata;
        private List<RsAareg> aareg;
        private List<OpprettSkattegrunnlag> sigrunstub;
        private InntektMultiplierWrapper inntektstub;
        private Arenadata arenaforvalter;
        private RsUdiPerson udistub;
        private RsInntektsmelding inntektsmelding;
        private PensjonData pensjonforvalter;
        private RsBregdata brregstub;
        private RsDokarkiv dokarkiv;
        private RsSykemelding sykemelding;
        private RsTpsMessaging tpsMessaging;
        private RsSkjerming skjerming;
    }
}