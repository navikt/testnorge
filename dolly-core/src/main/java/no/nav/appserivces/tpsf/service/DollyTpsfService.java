package no.nav.appserivces.tpsf.service;

import no.nav.appserivces.tpsf.domain.request.RsDollyPersonKriteriumRequest;
import no.nav.appserivces.tpsf.domain.response.RsSkdMeldingResponse;
import no.nav.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.repository.IdentRepository;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.service.TestgruppeService;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class DollyTpsfService {

    @Autowired
    TpsfApiService tpsfApiService;

    @Autowired
    TestgruppeService testgruppeService;

    @Autowired
    IdentRepository identRepository;

    public void opprettPersonerByKriterier(Long gruppeId, RsDollyPersonKriteriumRequest request){
        List<Object> klareIdenter = tpsfApiService.opprettPersonerTpsf(request);
        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

//        klareIdenter.forEach(ident -> {
//            RsSkdMeldingResponse response = tpsfApiService.sendTilTpsFraTPSF(ident, request.getEnvironments());
//
//            Testident testident = new Testident();
//            testident.setIdent(Long.parseLong(ident));
//            testident.setTestgruppe(testgruppe);
//            identRepository.save(testident);
//
//            //TODO oppdater oversikt over hva som har skjedd.
//
//            response.getSendSkdMeldingTilTpsResponsene().forEach(res -> {
//                System.out.println(res.getSkdmeldingstype());
//                request.getEnvironments().forEach(env -> {
//                    System.out.println(res.getStatus(env));
//                });
//            });
//
//        });

        String g = "g";

    }

    private HttpHeaders copyHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if ("connection".equals(headerName)) {
                headers.set(headerName, "keep-alive");
            } else {
                headers.set(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }
}
