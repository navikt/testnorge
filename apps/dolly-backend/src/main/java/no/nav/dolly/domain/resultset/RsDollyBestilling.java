package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.medl.RsMedl;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sigrunstub.RsLignetInntekt;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsDollyBestilling {

    private static final Set<String> EXCLUDE_METHODS = Set.of("getClass", "getMalBestillingNavn", "getEnvironments", "getPdldata");

    @Schema(description = "Sett av miljøer bestillingen skal deployes til")
    private Set<String> environments;

    @Schema(description = "Navn på malbestillling")
    private String malBestillingNavn;
    private PdlPersondata pdldata;
    private RsDigitalKontaktdata krrstub;
    private RsMedl medl;
    private List<RsInstdata> instdata;
    private List<RsAareg> aareg;
    private List<RsLignetInntekt> sigrunstub;
    private List<RsPensjonsgivendeForFolketrygden> sigrunstubPensjonsgivende;
    private InntektMultiplierWrapper inntektstub;
    private Arenadata arenaforvalter;
    private RsUdiPerson udistub;
    private PensjonData pensjonforvalter;
    private RsInntektsmelding inntektsmelding;
    private RsBregdata brregstub;
    private RsDokarkiv dokarkiv;
    private RsHistark histark;
    private RsSykemelding sykemelding;
    private RsTpsMessaging tpsMessaging;
    private BankkontoData bankkonto;
    private RsSkjerming skjerming;
    private ArbeidsplassenCVDTO arbeidsplassenCV;

    public List<RsAareg> getAareg() {
        if (isNull(aareg)) {
            aareg = new ArrayList<>();
        }
        return aareg;
    }

    public Set<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new HashSet<>();
        }
        return environments;
    }

    public List<RsLignetInntekt> getSigrunstub() {
        if (isNull(sigrunstub)) {
            sigrunstub = new ArrayList<>();
        }
        return sigrunstub;
    }

    public List<RsPensjonsgivendeForFolketrygden> getSigrunstubPensjonsgivende() {
        if (isNull(sigrunstubPensjonsgivende)) {
            sigrunstubPensjonsgivende = new ArrayList<>();
        }
        return sigrunstubPensjonsgivende;
    }

    public List<RsInstdata> getInstdata() {
        if (isNull(instdata)) {
            instdata = new ArrayList<>();
        }
        return instdata;
    }

    public static boolean isNonEmpty(RsDollyBestilling bestilling) {

        return Arrays.stream(RsDollyBestilling.class.getMethods())
                .filter(method -> "get".equals(method.getName().substring(0, 3)))
                .filter(method -> !EXCLUDE_METHODS.contains(method.getName()))
                .anyMatch(method -> {
                    try {
                        var object = method.invoke(bestilling);
                        return nonNull(object) && (!(object instanceof List) || !((List<?>) object).isEmpty());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return true;
                    }
                });
    }
}
