package no.nav.testnav.apps.tpsmessagingservice.service.skd;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.SkdMeldingTrans1;
import no.nav.testnav.apps.tpsmessagingservice.utils.ConvertDateToStringUtility;
import no.nav.testnav.apps.tpsmessagingservice.utils.IdenttypeFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DoedsmeldingBuilderService {

    private static final String AARSAKSKODE_FOR_DOEDSMELDING = "43";
    private static final String TRANSTYPE_FOR_DOEDSMELDING = "1";
    private static final String STATUSKODE_FNR = "5";
    private static final String STATUSKODE_DNR = "2";
    private static final String TILDELINGSKODE_DOEDSMELDING = "0";

    public SkdMeldingTrans1 build(DoedsmeldingRequest request) {

        var skdMeldingTrans1 = new SkdMeldingTrans1();
        skdMeldingTrans1.setTildelingskode(TILDELINGSKODE_DOEDSMELDING);

        addSkdParametersExtractedFromPerson(skdMeldingTrans1, request);

        return skdMeldingTrans1;
    }

    private void addSkdParametersExtractedFromPerson(SkdMeldingTrans1 skdMeldingTrans1, DoedsmeldingRequest request) {

        skdMeldingTrans1.setFodselsdato(request.getIdent().substring(0, 6));
        skdMeldingTrans1.setPersonnummer(request.getIdent().substring(6, 11));

        var datoDoed = ConvertDateToStringUtility.yyyyMMdd(request.getDoedsdato().atStartOfDay());

        skdMeldingTrans1.setMaskintid(ConvertDateToStringUtility.hhMMss(LocalDateTime.now()));
        skdMeldingTrans1.setMaskindato(ConvertDateToStringUtility.yyyyMMdd(LocalDateTime.now()));

        // The specification for doedsmelding says reg-dato should be doedsdato
        skdMeldingTrans1.setRegDato(datoDoed);
        skdMeldingTrans1.setDatoDoed(datoDoed);

        skdMeldingTrans1.setAarsakskode(AARSAKSKODE_FOR_DOEDSMELDING);
        skdMeldingTrans1.setStatuskode(Identtype.FNR == IdenttypeFraIdentUtility.getIdenttype(request.getIdent()) ?
                STATUSKODE_FNR : STATUSKODE_DNR);
        skdMeldingTrans1.setTranstype(TRANSTYPE_FOR_DOEDSMELDING);
    }
}
