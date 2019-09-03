package no.nav.registre.bisys.consumer.ui.modules;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.ActiveBisysPage;
import no.nav.bidrag.ui.bisys.kodeverk.KodeRolletype_Constants;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Barn;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Bidragsberegning;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Boforhold;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Inntekter;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Inntektslinje;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Periode;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Underholdskostnad;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.Vedtakslinje;
import no.nav.bidrag.ui.dto.BidragsmeldingConstant;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.ui.BisysUiConsumer;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;

@Slf4j
@Component
public class BisysUiFatteVedtakConsumer {

    public static final String KODE_BESL_AARSAK_FRITATT_IKKE_SOKT = "GIFR";
    public static final String KODE_BESL_AARSAK_ILAGT_IKKE_SOKT = "GIGI";
    public static final String INGEN_BARN_REGISTRERT_PAA_HUSSTAND = "Det er ikke registrert noen barn i husstanden.";

    public static final Map<String, String> beslAarsakDekodeMap() {

        Map<String, String> dekodeMap = new HashMap<>();
        dekodeMap.put(KODE_BESL_AARSAK_FRITATT_IKKE_SOKT, "Fritatt, ikke søkt");
        dekodeMap.put(KODE_BESL_AARSAK_ILAGT_IKKE_SOKT, "Ilagt, ikke søkt");

        return dekodeMap;
    }

    /**
     * Runs the Bidragsberegning and FatteVedtak process
     * 
     * <code>
     *  - Bisys entry point: Soknad
     *  - Bisys exit point: Fatte vedtak
     * </code>
     * 
     * @param bisys
     *            BisysApplication
     * @param request
     *            SynthesizedBidragRequest containing all data required to complete the FatteVedtak process for a søknad
     * @throws BidragRequestProcessingException
     */
    public void runFatteVedtak(BisysApplication bisys, SynthesizedBidragRequest request)
            throws BidragRequestProcessingException {

        ActiveBisysPage activePage = BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.SOKNAD);

        try {

            Soknad soknad = (Soknad) bisys.getActivePage(activePage);
            soknad.lagreOgBidrag().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Underholdskostnad underholdskostnad = (Underholdskostnad) bisys.getActivePage(activePage);
            underholdskostnad.lagreTilInntekter().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Inntekter inntekter = (Inntekter) bisys.getActivePage(activePage);
            fulfillInntekter(inntekter, request);
            inntekter.lagreTilBoforhold().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Boforhold boforhold = (Boforhold) bisys.getActivePage(activePage);
            fulfillBoforhold(boforhold, request);
            boforhold.lagreOgBidrag().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Bidragsberegning bidragsberegning = (Bidragsberegning) bisys.getActivePage(activePage);
            fulfillBidragsberegning(bidragsberegning, request);
            bidragsberegning.lagreOgBeregne().click();
            bidragsberegning.lagreOgFatteVedtak().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            FatteVedtak fatteVedtak = (FatteVedtak) bisys.getActivePage(activePage);
            completeFatteVedtak(fatteVedtak, request);
            fatteVedtak.executeFatteVedtak().click();

        } catch (ElementNotFoundException | NoSuchElementException | ClassCastException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }
    }

    /**
     * Updates the Inntekter view with required information.
     * 
     * <code>
     *  - Expected entry page: Inntekter
     *  - Expected exit page: Inntekter
     * </code>
     * 
     * @param inntekter
     * @param request
     * @throws BidragRequestProcessingException
     */
    private void fulfillInntekter(Inntekter inntekter, SynthesizedBidragRequest request)
            throws BidragRequestProcessingException {

        // BMs inntekter
        checkInntekter(KodeRolletype_Constants.BIDRAGSMOTTAKER, inntekter, request);
        inntekter.lagre().click();

        // BPs inntekter
        for (String option : inntekter.valgtRolle().getOptions()) {
            if (option.contains(request.getFnrBp())) {
                inntekter.selectValgtRolle(option);
                checkInntekter(KodeRolletype_Constants.BIDRAGSPLIKTIG, inntekter, request);
                inntekter.lagre().click();
            }
        }
    }

    private void checkInntekter(String rolletype, Inntekter inntekter, SynthesizedBidragRequest request) throws BidragRequestProcessingException {
        for (Inntektslinje inntektslinje : inntekter.inntektslinjer()) {
            try {
                if (!inntektslinje.brukInntekt().isEnabled()) {
                    inntektslinje.brukInntekt().toggle();
                }
            } catch (NoSuchElementException e) {
                if (request.getInntektBmEgneOpplysninger() > 0 && request.getInntektBpEgneOpplysninger() > 0) {

                    inntekter.leggTilInntekslinje().click();
                    for (Inntektslinje nyInntektslinje : inntekter.inntektslinjer()) {
                        LocalDate soktFra = LocalDate.parse(request.getSoktFra(), DateTimeFormat.forPattern("yyyy-MM-dd"));
                        soktFra.monthOfYear().withMinimumValue();
                        soktFra.dayOfMonth().withMaximumValue();
                        nyInntektslinje.gjelderFom().setValue(soktFra.toString("dd.MM.yyyy"));
                        nyInntektslinje.beskrivelse().select(Inntektslinje.KODE_PERSONINNTEKT_EGNE_OPPLYSNINGER);

                        int belop = KodeRolletype_Constants.BIDRAGSMOTTAKER.equals(rolletype) ? request.getInntektBmEgneOpplysninger() : request.getInntektBpEgneOpplysninger();
                        nyInntektslinje.belop().setValue(Integer.toString(belop));
                    }

                    inntekter.lagre().click();
                } else {
                    throw new BidragRequestProcessingException("No income registered!", inntekter, e);
                }
            }
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
        LocalDate soktFra = LocalDate.parse(request.getSoktFra(), DateTimeFormat.forPattern("yyyy-MM-dd"));

        if (!requestedBarnIncludedInList(barna, request.getFnrBa())) {
            boforhold.leggTilNyLinjeBarn().click();
            Barn nyttBarn = boforhold.barn().get(0);
            nyttBarn.medlemFnrInput().setValue(request.getFnrBa());

            nyttBarn.medlemFom().setValue(soktFra.toString("dd.MM.yyyy"));

            // Toggle registrert på adresse checkbox if enabled status differs from the requested status
            if (request.isBoforholdBarnRegistrertPaaAdresse() != nyttBarn.barnRegPaaAdrCbx()
                    .isEnabled()) {
                nyttBarn.barnRegPaaAdrCbx().toggle();
            }

            manageBarnRegPaaAdresseConstant(nyttBarn, boforhold.rolle().getText());
            nyttBarn.andelForsorget().select(request.getBoforholdAndelForsorging());
            nyttBarn.hentMedlem().click();

            return;

        } else {

            for (Barn barn : barna) {
                String fnrBarn = barn.medlemFnr().getText().replaceAll("\\s", "");

                if (fnrBarn.equals(request.getFnrBa())) {
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
                            .isBoforholdBarnRegistrertPaaAdresse()) {
                        barn.barnRegPaaAdrCbx().toggle();
                    }

                    manageBarnRegPaaAdresseConstant(barn, boforhold.rolle().getText());
                    barn.andelForsorget().select(request.getBoforholdAndelForsorging());

                    return;
                }
            }
        }

        throw new BidragRequestProcessingException(boforhold,
                new Exception(BisysUiConsumer.PROCESSING_FAILED + " Requested barn not found"));
    }

    // @TODO: Remove method once boforholdBarnRegistrertPaaAdresse has been made part of bidragsmelding.
    private void manageBarnRegPaaAdresseConstant(Barn barn, String rolle) {
        Field field;
        try {
            field = SynthesizedBidragRequest.class.getDeclaredField("boforholdBarnRegistrertPaaAdresse");

            if (BidragsmeldingConstant.class.equals(field.getAnnotation(BidragsmeldingConstant.class).annotationType())
                    && KodeRolletype_Constants.BIDRAGSMOTTAKER.equals(rolle)
                    && !barn.barnRegPaaAdrCbx().isEnabled()) {

                barn.barnRegPaaAdrCbx().toggle();

            }
        } catch (NoSuchFieldException | SecurityException e) {
            log.warn(
                    "@BidragsmeldingConstant annotation not found for field SynthesizedBidragRequest.boforholdBarnRegistrertPaaAdresse. "
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
    private void fulfillBidragsberegning(Bidragsberegning bidragsberegning,
            SynthesizedBidragRequest request) {

        bidragsberegning.selectKodeAarsak(request.getBidragsberegningKodeVirkAarsak());

        for (Periode periode : bidragsberegning.perioder()) {
            periode.samvaersklasseInput().setValue(request.getBidragsberegningSamvarsklasse());
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
    private void completeFatteVedtak(FatteVedtak fatteVedtak, SynthesizedBidragRequest request) {

        List<Vedtakslinje> vedtakslinjer = fatteVedtak.vedtakslinjer();

        for (Vedtakslinje vedtakslinje : vedtakslinjer) {
            // Selects vedtak result only if result-dropdown is present
            try {
                vedtakslinje.resultat();
            } catch (ElementNotFoundException | NoSuchElementException e1) {
                log.info("Label resultat not found on vedtakslinje. Testing for resultatDropdown.");
                try {
                    vedtakslinje.selectResultatDropdown(request.getFatteVedtakGebyrBeslAarsakKode());
                } catch (ElementNotFoundException | NoSuchElementException e2) {
                    log.info("resultatDropdown not found.");
                }
            }
        }
    }
}
