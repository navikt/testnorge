package no.nav.testnav.apps.tpsmessagingservice.service.skd;

import no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.SkdMeldingTrans1;
import no.nav.testnav.apps.tpsmessagingservice.utils.ConvertDateToStringUtility;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DoedsmeldingAnnulleringBuilderService {

    private static final String AARSAKS_KO_DE_FOR_DOEDSMELDING = "45";
    private static final String TRANSTYPE_FOR_DOEDSMELDING = "1";
    private static final String STATUS_KO_DE_FOR_DOEDSMELDING = "1";
    private static final String TILDELINGS_KO_DE_DOEDSMELDING_ANNULERING = "0";

    @Autowired
    private AdresseAppenderService adresseAppenderService;

    public SkdMeldingTrans1 execute(PersonDTO person) {

        var skdMeldingTrans1 = new SkdMeldingTrans1();
        skdMeldingTrans1.setTildelingskode(TILDELINGS_KO_DE_DOEDSMELDING_ANNULERING);

        addSkdParametersExtractedFromPerson(skdMeldingTrans1, person);
        adresseAppenderService.execute(skdMeldingTrans1, person);
        addDefaultParam(skdMeldingTrans1);

        return skdMeldingTrans1;
    }

    private void addSkdParametersExtractedFromPerson(SkdMeldingTrans1 skdMeldingTrans1, PersonDTO person) {

        skdMeldingTrans1.setFodselsdato(person.getIdent().substring(0, 6));
        skdMeldingTrans1.setPersonnummer(person.getIdent().substring(6, 11));

        var yyyyMMdd = ConvertDateToStringUtility.yyyyMMdd(LocalDateTime.now());
        var hhMMss = ConvertDateToStringUtility.hhMMss(LocalDateTime.now());

        skdMeldingTrans1.setMaskintid(hhMMss);
        skdMeldingTrans1.setMaskindato(yyyyMMdd);

        skdMeldingTrans1.setRegDato(yyyyMMdd);
    }

    private void addDefaultParam(SkdMeldingTrans1 skdMeldingTrans1) {

        skdMeldingTrans1.setAarsakskode(AARSAKS_KO_DE_FOR_DOEDSMELDING);
        skdMeldingTrans1.setTranstype(TRANSTYPE_FOR_DOEDSMELDING);
        skdMeldingTrans1.setStatuskode(STATUS_KO_DE_FOR_DOEDSMELDING);
    }
}
