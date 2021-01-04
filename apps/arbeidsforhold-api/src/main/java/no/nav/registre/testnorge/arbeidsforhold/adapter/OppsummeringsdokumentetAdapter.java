package no.nav.registre.testnorge.arbeidsforhold.adapter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.consumer.AaregSyntConsumer;
import no.nav.registre.testnorge.arbeidsforhold.domain.Oppsummeringsdokumentet;
import no.nav.registre.testnorge.arbeidsforhold.repository.OppsummeringsdokumentetRepository;
import no.nav.registre.testnorge.arbeidsforhold.repository.model.OppsummeringsdokumentetModel;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.EDAGM;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.ObjectFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class OppsummeringsdokumentetAdapter {
    private final AaregSyntConsumer aaregSyntConsumer;
    private final OppsummeringsdokumentetRepository opplysningspliktigRepository;


    public void save(Oppsummeringsdokumentet opplysningspliktig, String miljo) {

        String xml = opplysningspliktig.toXml();

        OppsummeringsdokumentetModel model = OppsummeringsdokumentetModel.builder()
                .document(xml)
                .orgnummer(opplysningspliktig.getOrgnummer())
                .year(opplysningspliktig.getRapporteringsmaaned().getYear())
                .month(opplysningspliktig.getRapporteringsmaaned().getMonthValue())
                .version(opplysningspliktig.getVersion())
                .miljo(miljo)
                .build();

        log.info("Oppretter oppsummeringsdokument for {}.", opplysningspliktig.getOrgnummer());
        aaregSyntConsumer.saveOpplysningspliktig(xml);
        opplysningspliktigRepository.save(model);
        log.info("Oppsummeringsdokument opprettet.");
    }

    public Oppsummeringsdokumentet fetch(String orgnummer, LocalDate rapporteringsmaaned, String miljo) {
        Optional<OppsummeringsdokumentetModel> opplysningspliktigModel = opplysningspliktigRepository
                .findBy(rapporteringsmaaned.getYear(), rapporteringsmaaned.getMonthValue(), orgnummer, miljo);
        if (opplysningspliktigModel.isEmpty()) {
            return null;
        }
        var model = opplysningspliktigModel.get();
        EDAGM edagm = toEDAGM(model.getDocument());
        return new Oppsummeringsdokumentet(edagm);
    }

    public List<Oppsummeringsdokumentet> fetchAll(String miljo) {
        return opplysningspliktigRepository
                .findAllByLast(miljo)
                .stream()
                .map(model -> new Oppsummeringsdokumentet(toEDAGM(model.getDocument())))
                .collect(Collectors.toList());
    }

    @SneakyThrows

    private static EDAGM toEDAGM(String xml) {
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        StringWriter sw = new StringWriter();
        sw.write(xml);
        JAXBElement<EDAGM> element = (JAXBElement<EDAGM>) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
        return element.getValue();
    }

}
