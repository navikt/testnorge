package no.nav.dolly.bestilling.krrstub;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;

@Slf4j
@Service
public class KrrstubClient implements ClientRegister {

    @Autowired
    private KrrstubConsumer krrstubConsumer;

    @Autowired
    private KrrstubResponseHandler krrstubResponseHandler;

    @Autowired
    private MapperFacade mapperFacade;

    @Override public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getKrrstub())) {

            try {
                DigitalKontaktdata digitalKontaktdata = mapperFacade.map(bestilling.getKrrstub(), DigitalKontaktdata.class);
                digitalKontaktdata.setPersonident(norskIdent.getIdent());

                deleteIdent(norskIdent.getIdent());

                ResponseEntity krrstubResponse = krrstubConsumer.createDigitalKontaktdata(digitalKontaktdata);
                progress.setKrrstubStatus(krrstubResponseHandler.extractResponse(krrstubResponse));

            } catch (RuntimeException e) {
                progress.setKrrstubStatus("Feil: " + e.getMessage());
                log.error("Kall til KrrStub feilet: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(ident -> deleteIdent(ident));
    }

    private void deleteIdent(String ident) {

        ResponseEntity<DigitalKontaktdata[]> response = krrstubConsumer.readDigitalKontaktdata(ident);

        if (response.hasBody()) {
            newArrayList(response.getBody()).forEach(dkif -> {
                if (nonNull(dkif.getId())) {
                    krrstubConsumer.deleteDigitalKontaktdata(dkif.getId());
                }
            });
        }
    }
}
