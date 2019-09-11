package no.nav.registre.bisys.consumer.ui;

import java.util.NoSuchElementException;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;

import lombok.AllArgsConstructor;
import net.morher.ui.connect.api.ApplicationDefinition;
import net.morher.ui.connect.api.listener.ActionLogger;
import net.morher.ui.connect.html.HtmlMapper;
import net.morher.ui.connect.html.listener.WaitForJavaScriptListener;
import net.morher.ui.connect.http.Browser;
import net.morher.ui.connect.http.BrowserConfigurer;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.BisysPageTitle;
import no.nav.bidrag.ui.bisys.common.BisysPage;
import no.nav.bidrag.ui.bisys.common.HeaderView;
import no.nav.bidrag.ui.bisys.exception.BisysUiConnectException;
import no.nav.bidrag.ui.bisys.sak.Sak;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.exception.BidragRequestRuntimeException;

@AllArgsConstructor
public class BisysUiSupport {

    String saksbehandlerUid;
    String saksbehandlerPwd;
    String bisysUrl;
    String rolleSaksbehandler;
    int enhet;

    public BisysApplication logon() {
        if (bisysUrl.isEmpty() || saksbehandlerUid.isEmpty() || saksbehandlerPwd.isEmpty() || rolleSaksbehandler.isEmpty()) {
            throw new BidragRequestRuntimeException("Logon information missing!");
        }

        return bisysLogon(bisysUrl, saksbehandlerUid, saksbehandlerPwd, rolleSaksbehandler, enhet);
    }

    /**
     * Opens Sak
     * 
     * <code>
     *  - Expected entry page: Sak
     *  - Expected exit page: Sak
     * </code>
     * 
     * @param bisys
     * @param saksnr
     * @throws BidragRequestProcessingException
     * @throws BisysUiConnectException
     */
    public static void getSak(BisysApplication bisys, String saksnr)
            throws BidragRequestProcessingException {

        checkCorrectActivePage(bisys, BisysPageTitle.SAK);
        Sak sak = (Sak) getActiveBisysPage(bisys);

        // Fill in saksnr
        sak.sokSaksnr().setValue(saksnr);

        // Click "Hent"
        sak.hentSak().click();
    }

    public static BisysPageTitle checkCorrectActivePage(BisysApplication bisys,
            BisysPageTitle expectedEntryPage) throws BidragRequestProcessingException {

        BisysPageTitle activePage = getBisysPageReference(bisys);

        if (activePage == null || !activePage.equals(expectedEntryPage)) {
            throw new BidragRequestProcessingException(bisys.bisysPage(),
                    new Exception(BisysUiConsumer.INCORRECT_ENTRY_PAGE));
        } else {
            return activePage;
        }
    }

    public static BisysPage getActiveBisysPage(BisysApplication bisys) throws BidragRequestProcessingException {
        BisysPageTitle pageRef = getBisysPageReference(bisys);
        try {
            return bisys.getActivePage(pageRef);
        } catch (BisysUiConnectException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(),
                    e);
        }
    }

    public static BisysPageTitle getBisysPageReference(BisysApplication bisys) throws BidragRequestProcessingException {
        try {
            return BisysPageTitle.getPageRef(bisys.getBisysPageTitle()).get();
        } catch (BisysUiConnectException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(),
                    e);
        }
    }

    /**
     * 
     * <code>
     *  - Expected entry page: Any page that contains header with link to Oppgaveliste 
     *  - Expected exit page: Sak
     * </code>
     * 
     * @param bisys
     */
    public static void redirectToSak(BisysApplication bisys) {

        try {

            HeaderView header = bisys.bisysPage().header();
            header.oppgavelister().click();
            bisys.bisysPage().header().velgSkjermbilde().select("Sak");

        } catch (ElementNotFoundException | NoSuchElementException e) {
            throw new BidragRequestRuntimeException(bisys.bisysPage(), e);
        }
    }

    /**
     * Logs on to Bisys.
     * 
     * @param bisysUrl
     * @param saksbehandlerUid
     * @param saksbehandlerPwd
     * @param rolleSaksbehandler
     * @param enhet
     * @return bisysApplication
     */
    public static BisysApplication bisysLogon(String bisysUrl, String saksbehandlerUid,
            String saksbehandlerPwd, String rolleSaksbehandler, int enhet) {

        BisysApplication bisys = openBrowser(bisysUrl);

        try {
            bisys.openamLoginPage().signIn(saksbehandlerUid, saksbehandlerPwd);

            // TODO: Introdusere mapping mellom saksbehandlers NAV-kontor og synt.brukers tilknyttede enhet
            // Velg pålogget enhet
            bisys.velgGruppe().velgGruppe(rolleSaksbehandler);
            bisys.velgEnhet().velgEnhet(enhet);

            return bisys;

        } catch (ElementNotFoundException | NoSuchElementException e) {
            throw new BidragRequestRuntimeException(bisys.bisysPage(), e);
        }
    }

    private static BisysApplication openBrowser(String url) {
        return ApplicationDefinition.of(BisysApplication.class).mapWith(new HtmlMapper())
                .addListener(new WaitForJavaScriptListener()).addListener(new ActionLogger())
                .connect(new Browser().asChrome().useInsecureSSL()
                        .addConfigurer(new BisysBrowserConfigurer()).openUrl(url));
    }

    private static class BisysBrowserConfigurer implements BrowserConfigurer {

        @Override
        public void configure(WebClient client) {
            client.getOptions().setThrowExceptionOnScriptError(false);
        }
    }
}
