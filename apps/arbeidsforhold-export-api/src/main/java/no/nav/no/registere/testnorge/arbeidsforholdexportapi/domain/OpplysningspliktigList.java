package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.EDAGM;

@Slf4j
@RequiredArgsConstructor
public class OpplysningspliktigList {
    private final List<EDAGM> list;

    @SuppressWarnings("unchecked")
    public static OpplysningspliktigList from(MultipartFile... files) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(EDAGM.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            List<EDAGM> list = new ArrayList<>();
            for (var file : files) {
                log.info("Converterer fil {}...", file.getOriginalFilename());
                var reader = new StringReader(new String(file.getBytes(), StandardCharsets.UTF_8));
                try {
                    list.add(((JAXBElement<EDAGM>) unmarshaller.unmarshal(reader)).getValue());
                } catch (Exception e) {
                    log.error("Klarer ikke a lese fil {}", file.getOriginalFilename());
                    throw e;
                }

            }
            return new OpplysningspliktigList(list);
        } catch (Exception e) {
            throw new RuntimeException("Klarer ikke aa convertere filene til EDAGM", e);
        }
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
