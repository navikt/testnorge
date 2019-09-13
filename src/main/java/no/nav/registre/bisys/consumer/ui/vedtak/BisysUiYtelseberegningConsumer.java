package no.nav.registre.bisys.consumer.ui.vedtak;

import static no.nav.registre.bisys.config.AppConfig.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.morher.ui.connect.api.element.Label;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.kodeverk.KodeRolletypeConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Barn;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Bidragsberegning;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Boforhold;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Forskuddsberegning;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Inntekter;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Inntektslinje;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Periode;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Skatteklasselinje;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.Vedtakslinje;
import no.nav.registre.bisys.consumer.rs.request.BidragsmeldingConstant;
import no.nav.registre.bisys.consumer.rs.request.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiConsumer;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;

@Slf4j
@Component
public class BisysUiYtelseberegningConsumer {

    public static final String INGEN_BARN_REGISTRERT_PAA_HUSSTAND = "Det er ikke registrert noen barn på husstanden.";
    private static final int SKATTEKLASSE_PAAKREVD_FOER_AAR = 2018;

    private enum InntekterFeedback {
        UGYLDIG_SKATTEKLASSE_REGEX(".+UGYLDIG SKATTEKLASSE"), INGEN_INNTEKTER_RETURNERT(".+INNTEKTSKOMPONENTEN RETURNERTE INGEN INNTEKTER."), INGEN_INNTEKTER_VALGT(".+INGEN INNTEKTER ER VALGT");

        private final String regex;

        InntekterFeedback(String regex) {
            this.regex = regex;
        }
    }

    public void fulfillForskuddsberegning(Forskuddsberegning forskuddsberegning, SynthesizedBidragRequest request) {

        forskuddsberegning.selectKodeArsak(request.getKodeVirkAarsak(request.getSoknadRequest().getSoktOm()));
        forskuddsberegning.selectSivilstand(request.getSivilstandBm());
        forskuddsberegning.selectUnntak(request.getKodeUnntForsk());

    }

    /**
     * Updates the Inntekter view with required information.
     * 
     * <code>
     *  - Expected entry page: Inntekter
     *  - Expected exit page: Inntekter
     * </code>
     * 
     * @param grKomKode
     * @param inntekter
     * @param request
     * @throws BidragRequestProcessingException
     */
    private void fulfillInntekter(Inntekter inntekter, SynthesizedBidragRequest request)
            throws BidragRequestProcessingException {

        // BMs inntekter
        checkRegisteredInntekter(KodeRolletypeConstants.BIDRAGSMOTTAKER, inntekter, request);
        inntekter.lagre().click();

        if (manualInntekterInfoRequired(inntekter)) {
            addInntektManually(KodeRolletypeConstants.BIDRAGSMOTTAKER, inntekter, request);
        }

        // BPs inntekter
        if (!KodeSoknGrKomConstants.FORSKUDD.equals(request.getSoknadRequest().getSoktOm())) {
            for (String option : inntekter.valgtRolle().getOptions()) {
                if (option.contains(request.getSoknadRequest().getFnrBp())) {
                    inntekter.selectValgtRolle(option);
                    checkRegisteredInntekter(KodeRolletypeConstants.BIDRAGSPLIKTIG, inntekter, request);
                    inntekter.lagre().click();
                    if (manualInntekterInfoRequired(inntekter)) {
                        addInntektManually(KodeRolletypeConstants.BIDRAGSPLIKTIG, inntekter, request);
                    }
                }
            }
        }
    }

    /**
     * 
     * Updates Inntekter and Boforhold
     * 
     * <code>
     *  - Expected entry page: Inntekter
     *  - Expected exit page: Boforhold
     * </code>
     * 
     * @param bisys
     * @param request
     * @throws BidragRequestProcessingException
     */
    public void fulfillInntekterAndBoforhold(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        Inntekter inntekter = (Inntekter) BisysUiSupport.getActiveBisysPage(bisys);
        fulfillInntekter(inntekter, request);
        inntekter.lagreTilBoforhold().click();

        Boforhold boforhold = (Boforhold) BisysUiSupport.getActiveBisysPage(bisys);
        fulfillBoforhold(boforhold, request);

        if (KodeSoknGrKomConstants.FORSKUDD.equals(request.getSoknadRequest().getSoktOm())) {
            boforhold.lagreOgForskudd().click();
        } else if (KodeSoknGrKomConstants.SARTILSKUDD.equals(request.getSoknadRequest().getSoktOm())
                || KodeSoknGrKomConstants.SARTILSKUDD_INNKREVING.equals(request.getSoknadRequest().getSoktOm())) {
            boforhold.lagreOgSartilskudd().click();
        } else {
            boforhold.lagreOgBidrag().click();
        }

    }

    private void checkRegisteredInntekter(String rolletype, Inntekter inntekter, SynthesizedBidragRequest request) throws BidragRequestProcessingException {
        for (Inntektslinje inntektslinje : inntekter.inntektslinjer()) {
            try {
                if (!inntektslinje.brukInntekt().isEnabled()) {
                    inntektslinje.brukInntekt().toggle();
                }
            } catch (NoSuchElementException e) {
                if (!rolleHarInntektEgneOpplysninger(rolletype, request)) {
                    throw new BidragRequestProcessingException("No income registered!", inntekter, e);
                }
            }
        }
    }

    private void addInntektManually(String rolletype, Inntekter inntekter, SynthesizedBidragRequest request) {

        inntekter.leggTilInntekslinje().click();
        Inntektslinje nyInntektslinje = inntekter.inntektslinjer().get(0);

        LocalDate soktFra = LocalDate.parse(request.getSoknadRequest().getSoktFra(), DateTimeFormat.forPattern("yyyy-MM-dd"));
        soktFra.monthOfYear().withMinimumValue();
        soktFra.dayOfMonth().withMaximumValue();
        nyInntektslinje.gjelderFom().setValue(soktFra.dayOfMonth().withMinimumValue().toString("dd.MM.yyyy"));
        nyInntektslinje.beskrivelse().select(Inntektslinje.KODE_PERSONINNTEKT_EGNE_OPPLYSNINGER);

        int belop = KodeRolletypeConstants.BIDRAGSMOTTAKER.equals(rolletype) ? request.getInntektBmEgneOpplysninger() : request.getInntektBpEgneOpplysninger();
        nyInntektslinje.belop().setValue(Integer.toString(belop));

        inntekter.lagre().click();

        if (feedbackMatchFound(InntekterFeedback.UGYLDIG_SKATTEKLASSE_REGEX.regex, inntekter.errors())) {
            addSkatteklasseIfMissing(inntekter, request);
        }
    }

    private boolean rolleHarInntektEgneOpplysninger(String rolletype, SynthesizedBidragRequest request) {
        if (KodeRolletypeConstants.BIDRAGSMOTTAKER.equals(rolletype)) {
            return request.getInntektBmEgneOpplysninger() > 0;
        } else if (KodeRolletypeConstants.BIDRAGSPLIKTIG.equals(rolletype)) {
            return request.getInntektBpEgneOpplysninger() > 0;
        }
        return false;
    }

    private boolean manualInntekterInfoRequired(Inntekter inntekter) {
        for (InntekterFeedback feedback : InntekterFeedback.values()) {
            if (feedbackMatchFound(feedback.regex, inntekter.messages())
                    || feedbackMatchFound(feedback.regex, inntekter.errors())) {
                return true;
            }
        }

        return false;
    }

    private boolean feedbackMatchFound(String regex, List<Label> feedback) {
        for (Label label : feedback) {
            String labelContent = label.getText();
            if (labelContent.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    private void addSkatteklasseIfMissing(Inntekter inntekter, SynthesizedBidragRequest request) {

        String soktFra = getSoktFra(inntekter, request);
        LocalDate soktFraDate = LocalDate.parse(soktFra, DateTimeFormat.forPattern(STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST));

        if (soktFraDate.getYear() < SKATTEKLASSE_PAAKREVD_FOER_AAR && !isSkatteklasseSetForSoktFraAr(inntekter, soktFraDate)) {
            addSkatteklasseForSoktFraAr(inntekter, soktFraDate.getYear(), request);
        }
    }

    private void addSkatteklasseForSoktFraAr(Inntekter inntekter, int year, SynthesizedBidragRequest request) {
        for (Skatteklasselinje linje : inntekter.skatteklasselinjer()) {
            String skattear = linje.skattear().getValue();
            if (skattear.isEmpty()) {
                linje.skattear().setValue(Integer.toString(year));
                linje.skatteklasse().setValue(Integer.toString(request.getSkatteklasse()));
                break;
            }
        }
    }

    private boolean isSkatteklasseSetForSoktFraAr(Inntekter inntekter, LocalDate soktFraDate) {
        for (Skatteklasselinje linje : inntekter.skatteklasselinjer()) {
            int skattear = linje.skattear().getValue().isEmpty() ? 0 : Integer.parseInt(linje.skattear().getValue());
            if (skattear == soktFraDate.getYear()) {
                return true;
            }
        }
        return false;
    }

    private String getSoktFra(Inntekter inntekter, SynthesizedBidragRequest request) {
        try {
            return inntekter.soktFraDato().getText();
        } catch (ElementNotFoundException | NoSuchElementException e) {
            return request.getSoknadRequest().getSoktFra();
        }
    }

    /**
     * Populates Boforhold page view with data.
     * 
     * <code>
     *  - Expected entry page: Boforhold
     *  - Expected exit page: Boforhold
     * </code>
     * 
     * @param boforhold
     * @param request
     * @throws BidragRequestProcessingException
     */
    private void fulfillBoforhold(Boforhold boforhold,
            SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        List<Barn> barna = boforhold.barn();
        LocalDate soktFra = LocalDate.parse(request.getSoknadRequest().getSoktFra(), DateTimeFormat.forPattern("yyyy-MM-dd")).dayOfMonth().withMinimumValue();

        if (!requestedBarnIncludedInList(barna, request.getSoknadRequest().getFnrBa())) {
            boforhold.leggTilNyLinjeBarn().click();
            Barn newBarn = boforhold.barn().get(0);
            newBarn.medlemFnrInput().setValue(request.getSoknadRequest().getFnrBa());
            updateBarnDetails(boforhold, request, soktFra, newBarn);
            newBarn.hentMedlem().click();

            return;

        } else {

            for (Barn barn : barna) {
                String fnrBarn = barn.medlemFnr().getText().replaceAll("\\s", "");

                if (fnrBarn.equals(request.getSoknadRequest().getFnrBa())) {
                    updateBarnDetails(boforhold, request, soktFra, barn);
                    return;
                }
            }
        }

        throw new BidragRequestProcessingException(boforhold,
                new Exception(BisysUiConsumer.PROCESSING_FAILED + " Requested barn not found"));
    }

    private void updateBarnDetails(Boforhold boforhold, SynthesizedBidragRequest request, LocalDate soktFra, Barn barn) {
        // Toggle bruk barn checkbox if not enabled
        if (!barn.inkluderMedlemCbx().isEnabled()) {
            barn.inkluderMedlemCbx().toggle();
        }

        // Date pattern dd.MM.yyyy
        Pattern fomDatePattern = Pattern.compile("^(0?[1-9]|[12][0-9]|3[01])[\\.](0?[1-9]|1[012])[\\.]\\d{4}$");

        String barnFomStr = barn.medlemFom().getValue().trim();

        // Set FOM date to the request's SOKT_FOM date if FOM date is missing
        if (!fomDatePattern.matcher(barnFomStr).matches()) {
            barn.medlemFom().setValue(soktFra.toString("dd.MM.yyyy"));
        }

        // Toggle registrert på adresse checkbox if enabled status differs from the requested status
        if (barn.barnRegPaaAdrCbx().isEnabled() != request
                .isBarnRegistrertPaaAdresse()) {
            barn.barnRegPaaAdrCbx().toggle();
        }

        manageBarnRegPaaAdresseConstant(barn, boforhold.rolle().getText());

        if (!KodeSoknGrKomConstants.FORSKUDD.equals(request.getSoknadRequest().getSoktOm())) {
            barn.andelForsorget().select(request.getAndelForsorging());
        }
    }

    // @TODO: Remove method once barnRegistrertPaaAdresse has been made part of bidragsmelding.
    private void manageBarnRegPaaAdresseConstant(Barn barn, String rolle) {
        Field field;
        try {
            field = SynthesizedBidragRequest.class.getDeclaredField("barnRegistrertPaaAdresse");

            if (BidragsmeldingConstant.class.equals(field.getAnnotation(BidragsmeldingConstant.class).annotationType())
                    && KodeRolletypeConstants.BIDRAGSMOTTAKER.equals(rolle)
                    && !barn.barnRegPaaAdrCbx().isEnabled()) {

                barn.barnRegPaaAdrCbx().toggle();

            }
        } catch (NoSuchFieldException | SecurityException e) {
            log.warn(
                    "@BidragsmeldingConstant annotation not found for field SynthesizedBidragRequest.barnRegistrertPaaAdresse. "
                            + "If this is due to the field being included in Bidragsmelding, this warning can safely be ignored.");
        }
    }

    private final boolean requestedBarnIncludedInList(List<Barn> barna, String fnrRequestedBarn) {
        for (Barn barn : barna) {
            try {
                String fnr = barn.medlemFnr().getText().replaceAll("\\s", "");
                if (fnr.equals(fnrRequestedBarn)) {
                    return true;
                }
            } catch (NoSuchElementException | ElementNotFoundException e) {
                log.info(INGEN_BARN_REGISTRERT_PAA_HUSSTAND);
            }
        }
        return false;
    }

    /**
     * Fills in values for page Bidragsberegning
     * 
     * <code>
     *  - Expected entry page: Bidragsberegning
     *  - Expected exit page: Bidragsberegning
     * </code>
     * 
     * @param bidragsberegning
     * @param request
     */
    public void fulfillBidragsberegning(Bidragsberegning bidragsberegning,
            SynthesizedBidragRequest request) {

        bidragsberegning.selectKodeAarsak(request.getKodeVirkAarsak(request.getSoknadRequest().getSoktOm()));

        for (Periode periode : bidragsberegning.perioder()) {
            periode.samvaersklasseInput().setValue(request.getSamvarsklasse());
        }
    }

    /**
     * 
     * Fills in data for the Fatte Vedtak page
     * 
     * <code>
     *  - Expected entry page: Fatte Vedtak
     *  - Expected exit page: Fatte Vedtak
     * </code>
     * 
     * @param fatteVedtak
     * @param request
     */
    public void completeFatteVedtak(FatteVedtak fatteVedtak, SynthesizedBidragRequest request) {

        List<Vedtakslinje> vedtakslinjer = fatteVedtak.vedtakslinjer();

        for (Vedtakslinje vedtakslinje : vedtakslinjer) {
            // Selects vedtak result only if result-dropdown is present
            try {
                vedtakslinje.resultat();
            } catch (ElementNotFoundException | NoSuchElementException e1) {
                log.info("Label resultat not found on vedtakslinje. Testing for resultatDropdown.");
                try {
                    vedtakslinje.selectResultatDropdown(request.getGebyrBeslAarsakKode());
                } catch (ElementNotFoundException | NoSuchElementException e2) {
                    log.info("resultatDropdown not found.");
                }
            }
        }
    }

}
