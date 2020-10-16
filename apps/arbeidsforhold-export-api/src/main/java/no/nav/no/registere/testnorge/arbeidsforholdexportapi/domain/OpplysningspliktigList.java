package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.EDAGM;

@Slf4j
@RequiredArgsConstructor
public class OpplysningspliktigList {
    private final List<EDAGM> list;

    @SuppressWarnings("unchecked")
    private static EDAGM from(String xml, Unmarshaller unmarshaller) throws JAXBException {
        var reader = new StringReader(xml);
        return ((JAXBElement<EDAGM>) unmarshaller.unmarshal(reader)).getValue();
    }

    public static OpplysningspliktigList from(MultipartFile... files) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(EDAGM.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            List<EDAGM> list = new ArrayList<>();
            for (var file : files) {
                log.info("Converterer fil {}...", file.getOriginalFilename());
                try {
                    list.add(from(new String(file.getBytes(), StandardCharsets.UTF_8), unmarshaller));
                } catch (Exception e) {
                    log.error("Klarer ikke a lese fil {}", file.getOriginalFilename());
                    throw e;
                }
            }
            return new OpplysningspliktigList(list);
        } catch (Exception e) {
            throw new RuntimeException("Klarer ikke a convertere filene til EDAGM", e);
        }
    }

    public static OpplysningspliktigList from(List<String> xmls) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(EDAGM.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            List<EDAGM> list = new ArrayList<>();
            for (var xml : xmls) {
                try {
                    list.add(from(xml, unmarshaller));
                } catch (Exception e) {
                    log.error("Klarer ikke a convertere xml");
                    throw e;
                }
            }
            return new OpplysningspliktigList(list);
        } catch (Exception e) {
            throw new RuntimeException("Klarer ikke a convertere xmlene til EDAGM", e);
        }
    }

    public int getAntallPersoner() {
        return toArbeidsforhold().stream().collect(Collectors.groupingBy(Arbeidsforhold::getIdent)).keySet().size();
    }

    public List<Arbeidsforhold> toArbeidsforhold() {
        List<Arbeidsforhold> arbeidsforholds = new ArrayList<>();
        list.stream().map(EDAGM::getLeveranse).forEach(leveranse ->
                leveranse.getOppgave().getVirksomhet().forEach(virksomhet ->
                        virksomhet.getInntektsmottaker().forEach(inntektsmottaker ->
                                inntektsmottaker.getArbeidsforhold().forEach(arbeidsforhold ->
                                        arbeidsforholds.add(
                                                new Arbeidsforhold(
                                                        leveranse,
                                                        virksomhet,
                                                        inntektsmottaker,
                                                        arbeidsforhold
                                                )
                                        )
                                )
                        )
                )
        );
        return arbeidsforholds;
    }
}
