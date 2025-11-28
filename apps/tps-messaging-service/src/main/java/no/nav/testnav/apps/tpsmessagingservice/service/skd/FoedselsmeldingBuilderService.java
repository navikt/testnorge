package no.nav.testnav.apps.tpsmessagingservice.service.skd;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.SkdMeldingTrans1;
import no.nav.testnav.apps.tpsmessagingservice.utils.ConvertDateToStringUtility;
import no.nav.testnav.apps.tpsmessagingservice.utils.HentDatoFraIdentUtility;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class FoedselsmeldingBuilderService {

    private static final String AARSAKSKODE_FOR_FOEDSELSMELDING = "01";
    private static final String TILDELINGSKODE_FOEDSELSMELDING = "0";

    private final AdresseAppenderService adresseAppenderService;

    public SkdMeldingTrans1 build(FoedselsmeldingRequest request) {

        var skdMeldingTrans1 = new SkdMeldingTrans1();
        skdMeldingTrans1.setTildelingskode(TILDELINGSKODE_FOEDSELSMELDING);

        addSkdParametersExtractedFromPerson(skdMeldingTrans1, request.getBarn());
        addSkdParametersExtractedFromForeldre(skdMeldingTrans1, request);
        addDefaultParam(skdMeldingTrans1);

        return skdMeldingTrans1;
    }

    private void addSkdParametersExtractedFromPerson(SkdMeldingTrans1 skdMeldingTrans1, PersonDTO barn) {

        var regdato = ConvertDateToStringUtility.yyyyMMdd(HentDatoFraIdentUtility.extract(barn.getIdent()));

        skdMeldingTrans1.setFodselsdato(getDato(barn));
        skdMeldingTrans1.setPersonnummer(getPersonnr(barn));

        skdMeldingTrans1.setMaskintid("000000");
        skdMeldingTrans1.setMaskindato(ConvertDateToStringUtility.yyyyMMdd(LocalDateTime.now()));
        skdMeldingTrans1.setRegDato(regdato);

        skdMeldingTrans1.setFoedekommLand("0301");
        skdMeldingTrans1.setFoedested("Sykehus");

        skdMeldingTrans1.setFornavn(barn.getFornavn());
        skdMeldingTrans1.setSlektsnavn(barn.getEtternavn());
        skdMeldingTrans1.setKjoenn(barn.getKjonn());

        skdMeldingTrans1.setRegdatoAdr(regdato);
        skdMeldingTrans1.setPersonkode("3");
        skdMeldingTrans1.setLevendeDoed("1");
        skdMeldingTrans1.setStatuskode("1");

        skdMeldingTrans1.setSpesRegType(barn.getSpesreg());
        skdMeldingTrans1.setDatoSpesRegType(regdato);
    }

    private void addSkdParametersExtractedFromForeldre(SkdMeldingTrans1 skdMeldingTrans1, FoedselsmeldingRequest request) {

        if (isNull(request.getMor())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, request.getBarn().getIdent() + " mangler en mor i fødselsmeldingen.");
        }

        skdMeldingTrans1.setMorsFodselsdato(getDato(request.getMor()));
        skdMeldingTrans1.setMorsPersonnummer(getPersonnr(request.getMor()));
        skdMeldingTrans1.setSlektsnavn(request.getBarn().getEtternavn());
        skdMeldingTrans1.setFamilienummer(request.getMor().getIdent());
        skdMeldingTrans1.setRegdatoFamnr(
                ConvertDateToStringUtility.yyyyMMdd(HentDatoFraIdentUtility.extract(request.getBarn().getIdent())));

        if (nonNull(request.getFar())) {
            skdMeldingTrans1.setForeldreansvar("D");
            skdMeldingTrans1.setFarsFodselsdato(getDato(request.getFar()));
            skdMeldingTrans1.setFarsPersonnummer(getPersonnr(request.getFar()));
        } else {
            skdMeldingTrans1.setForeldreansvar("M");
        }
        skdMeldingTrans1.setDatoForeldreansvar(
                ConvertDateToStringUtility.yyyyMMdd(HentDatoFraIdentUtility.extract(request.getBarn().getIdent())));

        adresseAppenderService.execute(skdMeldingTrans1, request.getBarn());
    }

    private void addDefaultParam(SkdMeldingTrans1 skdMeldingTrans1) {
        skdMeldingTrans1.setSivilstand("1");
        skdMeldingTrans1.setTranstype("1");
        skdMeldingTrans1.setAarsakskode(AARSAKSKODE_FOR_FOEDSELSMELDING);
    }

    private String getPersonnr(PersonDTO person) {

        return person.getIdent().substring(6, 11);
    }

    private static String getDato(PersonDTO person) {

        return person.getIdent().substring(0, 6);
    }
}
