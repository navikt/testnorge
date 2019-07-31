package no.nav.dolly.bestilling.instdata;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.InstdataInstitusjonstype;
import no.nav.dolly.domain.resultset.inst.InstdataKategori;
import no.nav.dolly.domain.resultset.inst.InstdataKilde;

@Service
public class InstdataClient implements ClientRegister {

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private InstdataConsumer instdataConsumer;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getInstdata())) {

            List<Instdata> instdataListe = mapperFacade.mapAsList(bestilling.getInstdata(), Instdata.class);
            instdataListe.forEach(instdata -> {
                instdata.setPersonident(norskIdent.getIdent());

                instdata.setKategori(nullcheckSetDefaultValue(instdata.getKategori(), decideKategori(instdata.getInstitusjonstype())));
                instdata.setKilde(nullcheckSetDefaultValue(instdata.getKilde(), getKilde(instdata.getInstitusjonstype())));
                instdata.setOverfoert(nullcheckSetDefaultValue(instdata.getOverfoert(), false));
                instdata.setTssEksternId(nullcheckSetDefaultValue(instdata.getTssEksternId(), getTssEksternId(instdata.getInstitusjonstype())));
            });

            ResponseEntity<InstdataResponse> instdataResponse = instdataConsumer.getInstdata(norskIdent.getIdent());
            if (instdataResponse.hasBody() && !instdataResponse.getBody().getIdentliste().isEmpty()) {
                //TODO
            }
        }
    }

    private static InstdataKategori decideKategori(InstdataInstitusjonstype type) {

        switch (type) {
        case AS:
            return InstdataKategori.A;
        case FO:
            return InstdataKategori.S;
        case HS:
        default:
            return InstdataKategori.R;
        }
    }

    private static InstdataKilde getKilde(InstdataInstitusjonstype type) {

        switch (type) {
        case AS:
            return InstdataKilde.PP01;
        case FO:
            return InstdataKilde.IT;
        case HS:
        default:
            return InstdataKilde.INST;
        }
    }

    private static String getTssEksternId(InstdataInstitusjonstype type) {

        switch (type) {
        case AS:
            return "80000464106"; // ADAMSTUEN SYKEHJEM
        case FO:
            return "80000465653"; // INDRE ØSTFOLD FENGSEL
        case HS:
        default:
            return "80000464241"; // HELGELANDSSYKEHUSET HF
        }
    }
}
