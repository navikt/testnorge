package no.nav.dolly.bestilling.histark.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.domain.jpa.Dokument;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.mapper.MappingStrategy;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static no.nav.dolly.bestilling.dokarkiv.mapper.PdfVedlegg.PDF_VEDLEGG;

@Slf4j
@Component
public class HistarkMappingStrategy implements MappingStrategy {

    private static final String PERSON_IDENT = "personIdent";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsHistark.RsHistarkDokument.class, HistarkRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsHistark.RsHistarkDokument dokument, HistarkRequest histarkRequest, MappingContext context) {

                        var dokumenter = (List<Dokument>) context.getProperty("dokumenter");

                            var fysiskDokument = dokumenter.stream()
                                    .filter(dok -> dokument.getDokumentReferanse().equals(dok.getId()))
                                    .map(Dokument::getContents)
                                    .findFirst()
                                    .orElse(PDF_VEDLEGG);

                            histarkRequest.setFile(new ByteArrayResource(fysiskDokument.getBytes(StandardCharsets.UTF_8)) {
                                @Override
                                public String getFilename() {
                                    return dokument.getTittel();
                                }
                            });
                            histarkRequest.setMetadata(HistarkRequest.HistarkMetadata.builder()
                                    .antallSider(String.valueOf(dokument.getAntallSider()))
                                    .brukerident(((String) context.getProperty(PERSON_IDENT)))
                                    .enhetsnavn(dokument.getEnhetsnavn())
                                    .enhetsnummer(dokument.getEnhetsnummer())
                                    .filnavn(dokument.getTittel())
                                    .skanner(dokument.getSkanner())
                                    .skannested(dokument.getSkannested())
                                    .klage("")
                                    .sjekksum(calculateBinaryChecksum(fysiskDokument))
                                    .skanningstidspunkt(dokument.getSkanningsTidspunkt().format(dateTimeFormatter))
                                    .startAar(String.valueOf(dokument.getStartYear()))
                                    .sluttAar(String.valueOf(dokument.getEndYear()))
                                    .temakoder(String.join(",", dokument.getTemakoder()))
                                    .build());
                    }
                })
                .register();
    }

    private static String calculateBinaryChecksum(String fysiskDokument) {
        InputStream byteArrayInput = new ByteArrayInputStream(fysiskDokument.getBytes());
        InputStream base64Input = new Base64InputStream(byteArrayInput);

        try {
            return DigestUtils.sha256Hex(base64Input);
        } catch (IOException e) {
            throw new DollyFunctionalException("Klarte ikke å kalkulere sjekksum for innsendt histark dokument", e);
        }
    }
}