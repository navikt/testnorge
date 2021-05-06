package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.v2_0;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.EDAGM;

@Slf4j
@RequiredArgsConstructor
public class Opplysningspliktig {
    private final EDAGM edagm;
    private static Unmarshaller unmarshaller;

    static {
        try {
            var jaxbContext = JAXBContext.newInstance(EDAGM.class);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            log.error("Feil ved opprettelse av unmarshaller for EDAGM", e);
        }
    }


    @SuppressWarnings("unchecked")
    private static EDAGM from(String xml, Unmarshaller unmarshaller) throws JAXBException {
        try (var reader = new StringReader(xml)) {
            EDAGM edagm = ((JAXBElement<EDAGM>) unmarshaller.unmarshal(reader)).getValue();
            reader.close();
            return edagm;
        }
    }

    public static Opplysningspliktig from(String xml) {
        try {
            return new Opplysningspliktig(from(xml, unmarshaller));
        } catch (Exception e) {
            throw new RuntimeException("Klarer ikke a konvertere xmlene til EDAGM", e);
        }
    }

    public List<Avvik> toAvvik() {
        var avviks = new ArrayList<Avvik>();
        avviks.addAll(
                getArbeidsforhold()
                        .stream()
                        .filter(Arbeidsforhold::hasAvvik)
                        .map(Arbeidsforhold::getAvvikList)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
        avviks.addAll(
                getPermisjoner()
                        .stream()
                        .filter(Permisjon::hasAvvik)
                        .map(Permisjon::getAvvikList)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );

        avviks.addAll(
                getInntekter()
                        .stream()
                        .filter(Inntekt::hasAvvik)
                        .map(Inntekt::getAvvikList)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
        return avviks;
    }

    private List<Permisjon> getPermisjoner() {
        return getArbeidsforhold()
                .stream()
                .map(Arbeidsforhold::getPermisjoner)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    public List<Permisjon> toPermisjoner() {
        return getPermisjoner()
                .stream()
                .filter(value -> !value.hasAvvik())
                .collect(Collectors.toList());
    }

    public List<Arbeidsforhold> toArbeidsforhold() {
        return getArbeidsforhold()
                .stream()
                .filter(value -> !value.hasAvvik())
                .collect(Collectors.toList());
    }

    private List<Arbeidsforhold> getArbeidsforhold() {
        List<Arbeidsforhold> arbeidsforholds = new ArrayList<>();
        var leveranse = this.edagm.getLeveranse();
        leveranse.getOppgave().getVirksomhet().forEach(virksomhet ->
                virksomhet.getInntektsmottaker().forEach(inntektsmottaker -> {
                    var value = new Inntektsmottaker(leveranse, virksomhet, inntektsmottaker);
                    arbeidsforholds.addAll(value.getArbeidsforholdList());
                })
        );
        return arbeidsforholds;
    }

    public List<Inntekt> toInntekt() {
        return getInntekter()
                .stream()
                .filter(value -> !value.hasAvvik())
                .collect(Collectors.toList());
    }

    private List<Inntekt> getInntekter() {
        List<Inntekt> inntekts = new ArrayList<>();
        var leveranse = this.edagm.getLeveranse();
        leveranse.getOppgave().getVirksomhet().forEach(virksomhet ->
                virksomhet.getInntektsmottaker().forEach(inntektsmottaker -> {
                    var value = new Inntektsmottaker(leveranse, virksomhet, inntektsmottaker);
                    inntekts.addAll(value.getInntektList());
                })
        );
        return inntekts;
    }
}
